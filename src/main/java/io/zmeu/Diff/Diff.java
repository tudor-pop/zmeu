package io.zmeu.Diff;

import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Utils.Reflections;
import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;

/**
 *
 */
@Log4j2
public class Diff {
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
        validate(base, left, right);

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
            updateReadOnlyProperties(merged, left);
        }

        var diff = this.javers.compare(merged, left);


        if (merged == null) {
            merged = left;
        } else {
            mapper.map(left, merged);
        }

        return new MergeResult(diff.getChanges(), merged);
    }

    private static void updateReadOnlyProperties(Resource source, Resource target) {
        for (Field property : target.getClass().getDeclaredFields()) {
            if (Reflections.isReadOnly(property)) {
                try {
                    Field field = source.getClass().getDeclaredField(property.getName());
                    field.setAccessible(true);
                    property.setAccessible(true);
                    property.set(target, field.get(source));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void validate(@Nullable Resource localState, Resource sourceState, @Nullable Resource cloudState) {
        if (localState != null && StringUtils.isBlank(localState.getResourceName())) {
            throw new IllegalArgumentException(localState + " is missing resource name");
        }
        if (sourceState != null && StringUtils.isBlank(sourceState.getResourceName())) {
            throw new IllegalArgumentException(sourceState + " is missing resource name");
        }
        if (cloudState != null && StringUtils.isBlank(cloudState.getResourceName())) {
            throw new IllegalArgumentException(cloudState + " is missing resource name");
        }
    }

    @SneakyThrows
    public Plan apply(Plan plan, PluginFactory pluginFactory) {
        var changeProcessor = new ResourceChangeLog(true);

        for (MergeResult mergeResult : plan.getMergeResults()) {
            Changes changes1 = mergeResult.changes();
            javers.processChangeList(changes1, changeProcessor);

            for (ChangesByObject changes : changes1.groupByObject()) {
                String typeName = changes.getGlobalId().getTypeName();
                var pluginRecord = pluginFactory.getPluginHashMap().get(typeName);

                Provider provider = pluginRecord.provider();

                if (!changes.getNewObjects().isEmpty()) {
                    provider.create(mergeResult.resource());

                } else if (!changes.getObjectsRemoved().isEmpty()) {
                    provider.delete(mergeResult.resource());

                } else {
                    changeProcessor.setType(ResourceChange.CHANGE);
                    provider.update(mergeResult.resource());
                }

            }
            javers.commit("Tudor", mergeResult.resource());
        }
        return plan;
    }

}
