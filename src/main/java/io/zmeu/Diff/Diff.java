package io.zmeu.Diff;

import io.zmeu.Plugin.PluginFactory;
import io.zmeu.api.Provider;
import io.zmeu.api.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;

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
     * @param baseState   javers state stored in the database
     * @param sourceState source code state
     * @param cloudState  state read from the cloud like a VM
     * @return
     */
    @SneakyThrows
    public MergeResult merge(@Nullable Resource baseState/* javers state*/, Resource sourceState, @Nullable Resource cloudState) {
        validate(baseState, sourceState, cloudState);

        if (cloudState == null) {
            // when cloud state has been removed, localstate/javers data must be invalidated since it's out of date and
            // the source code becomes the source of truth
//            baseState = null;
        }
        if (baseState != null && cloudState != null) {
            mapper.map(cloudState, baseState);
        }
        var diff = this.javers.compare(baseState, sourceState);
//        var changes = javers.processChangeList(diff.getChanges(), new ResourceChangeLog(true));
        baseState = baseState == null ? sourceState : baseState;
        mapper.map(sourceState, baseState);
        return new MergeResult(diff.getChanges(), baseState);
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
