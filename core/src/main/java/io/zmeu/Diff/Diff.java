package io.zmeu.Diff;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;

import java.util.HashMap;

/**
 *
 */
@Log4j2
public class Diff {
    @Getter
    private Javers javers;
    private ModelMapper mapper;

    @SneakyThrows
    public Diff(Javers javers, ModelMapper mapper) {
        this();
        this.javers = javers;
        this.mapper = mapper;
    }

    public Diff() {

    }

    /**
     * make a 3-way merge between the resources
     *
     * @param base  javers state stored in the database
     * @param left  source code state
     * @param right state read from the cloud like a VM
     * @return
     */
    @SneakyThrows
    public MergeResult merge(@Nullable Resource base, Resource left, @Nullable Resource right) {
        DiffUtils.validate(base);
        DiffUtils.validate(left);
        DiffUtils.validate(right);

        var merged = base == null ? left : base;
        if (right != null) {
            /**
             * accept right/theirs/cloud changes. Any undeclared properties in the state (like unique cloud ids)
             * will be set on the base since they are probably already out of date in the base state
             * */
            mapper.map(right, merged);
        } else {
            merged = null;
        }
        // Preserve cloud-managed properties explicitly.
        // Src fields must get the cloud values because they are not explicitly set in code but rather set by the cloud provider(read only properties)
        if (merged != null && left != null) {
            DiffUtils.updateReadOnlyProperties(merged, left);
        }

        var diff = this.javers.compare(merged, left);


        if (merged == null) {
            merged = left;
        } else if (left != null) {
            mapper.map(left, merged);
        } else { // on object removed (src doesn't exist because it was removed) create an empty object of the same type
            merged = merged.getClass().newInstance();
        }

        return new MergeResult(diff.getChanges(), merged);
    }

    @SneakyThrows
    public Plan apply(Plan plan, HashMap<String, Provider> map) {
        var changeProcessor = new ResourceChangeLog(true);

        for (MergeResult mergeResult : plan.getMergeResults()) {
            Changes changes1 = mergeResult.changes();
            javers.processChangeList(changes1, changeProcessor);

            for (ChangesByObject changes : changes1.groupByObject()) {
                var provider = map.get(mergeResult.resource().getType());

                apply(mergeResult, changes, provider, changeProcessor);
            }
            javers.commit("Tudor", mergeResult.resource());
        }
        return plan;
    }

    private static void apply(MergeResult mergeResult, ChangesByObject changes, Provider provider, ResourceChangeLog changeProcessor) {
        if (!changes.getNewObjects().isEmpty()) {
            provider.create(mergeResult.resource());
        } else if (!changes.getObjectsRemoved().isEmpty()) {
            provider.delete(mergeResult.resource());
        } else {
            changeProcessor.setType(ResourceChange.CHANGE);
            provider.update(mergeResult.resource());
        }
    }

}
