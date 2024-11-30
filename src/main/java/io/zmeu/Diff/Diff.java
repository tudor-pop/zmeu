package io.zmeu.Diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.api.Provider;
import io.zmeu.api.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
@Log4j2
public class Diff {
    private Javers javers;
    private ObjectMapper mapper;

    @SneakyThrows
    public Diff(Javers javers, ObjectMapper mapper) {
        this();
        this.javers = javers;
        this.mapper = mapper;
    }

    public Diff() {

    }

    @SneakyThrows
    public DiffResult changes(@Nullable Resource baseState/* javers state*/, Resource sourceState, @Nullable Resource cloudState) {
        validate(baseState, sourceState, cloudState);

        if (cloudState == null) {
            // when cloud state has been removed, localstate/javers data must be invalidated since it's out of date and
            // the source code becomes the source of truth
            baseState = null;
        }
        if (baseState != null) {
            mapper.readerForUpdating(baseState).readValue((JsonNode) mapper.valueToTree(cloudState));
        }
        var diff = this.javers.compare(baseState, sourceState);
//        var changes = javers.processChangeList(diff.getChanges(), new ResourceChangeLog(true));
        baseState = baseState == null ? sourceState : baseState;
        return new DiffResult(diff.getChanges(), baseState);
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

        for (DiffResult diffResult : plan.getDiffResults()) {
            javers.processChangeList(diffResult.getChanges(), changeProcessor);

            for (ChangesByObject changes : diffResult.getChanges().groupByObject()) {
                String typeName = changes.getGlobalId().getTypeName();
                var pluginRecord = pluginFactory.getPluginHashMap().get(typeName);

                Provider provider = pluginRecord.provider();

                if (!changes.getNewObjects().isEmpty()) {
                    provider.create(diffResult.getResource());
                } else if (!changes.getObjectsRemoved().isEmpty()) {
                    changeProcessor.onObjectRemoved(changes.getObjectsRemoved().get(0));
                    provider.delete(diffResult.getResource());
                } else if (changes.getNewObjects().isEmpty() && changes.getObjectsRemoved().isEmpty()) {
                    changeProcessor.setType(ResourceChange.CHANGE);

                    provider.update(diffResult.getResource());
                }

            }
            //            for (NewObject newObject : diffResult.getNewObjects()) {
            //                changeProcessor.onNewObject(newObject);
            //            }
            //            for (ObjectRemoved objectRemoved : diffResult.getObjectsRemoved()) {
            //                changeProcessor.onObjectRemoved(objectRemoved);
            //            }
//                    changeProcessor.setType(ResourceChange.CHANGE);
//                    String typeName = changes.getGlobalId().getTypeName();
//                    var pluginRecord = pluginFactory.getPluginHashMap().get(typeName);


//            javers.processChangeList(diffResult.getChanges(), new ResourceChangeLog(true));
            //            for (PropertyChange propertyChange : diffResult.getPropertyChanges()) {
            //                textChangeLog.onPropertyChange(propertyChange);
            //            }
            javers.commit("Tudor", diffResult.getResource());
        }
        return plan;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
