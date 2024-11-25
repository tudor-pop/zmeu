package io.zmeu.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.api.Resource;
import io.zmeu.javers.ResourceApplyPlan;
import io.zmeu.javers.ResourceChangeLog;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
    public Plan plan(@Nullable Resource javersState, Resource sourceState, @Nullable Resource cloudState) {
        validate(javersState, sourceState, cloudState);

        if (cloudState == null) {
            // when cloud state has been removed, localstate/javers data must be invalidated since it's out of date and
            // the source code becomes the source of truth
            javersState = null;
        }

        if (javersState != null) {
            mapper.readerForUpdating(javersState).readValue((JsonNode) mapper.valueToTree(cloudState));
        }
        var diff = this.javers.compare(javersState, sourceState);
//        var changes = javers.processChangeList(diff.getChanges(), new ResourceChangeLog(true));
        javersState = handleNullState(javersState);
        return new Plan(sourceState, diff.groupByObject());
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

    private static Resource handleNullState(@Nullable Resource localState) {
        return localState == null ? Resource.builder().build() : localState;
    }

    @SneakyThrows
    public Plan apply(Resource javersState, Plan plan, PluginFactory pluginFactory) {
        var changeProcessor = new ResourceApplyPlan(pluginFactory);

        for (ChangesByObject diffResult : plan.diffResults()) {
//            for (NewObject newObject : diffResult.getNewObjects()) {
//                changeProcessor.onNewObject(newObject);
//            }
//            for (ObjectRemoved objectRemoved : diffResult.getObjectsRemoved()) {
//                changeProcessor.onObjectRemoved(objectRemoved);
//            }
            if (diffResult.getPropertyChanges().size() > 0) {
                changeProcessor.setType(ResourceChange.CHANGE);
                String typeName = diffResult.getGlobalId().getTypeName();
                var pluginRecord = pluginFactory.getPluginHashMap().get(typeName);

                Provider provider = pluginRecord.provider();
                provider.update(javersState, plan.sourceCode());
            } else{
                javers.processChangeList(diffResult.get(), changeProcessor);
            }
//            for (PropertyChange propertyChange : diffResult.getPropertyChanges()) {
//                textChangeLog.onPropertyChange(propertyChange);
//            }
            javers.commit("Tudor", plan.sourceCode());
        }
        return plan;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
