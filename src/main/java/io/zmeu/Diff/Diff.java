package io.zmeu.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zmeu.Plugin.PluginFactory;
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

/**
 *
 */
@Log4j2
public class Diff {
    private Javers javers;
    @Getter
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @SneakyThrows
    public Diff(Javers javers) {
        this();
        this.javers = javers;
    }

    public Diff() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            mapper.setAccessorNaming( new DefaultAccessorNamingStrategy.Provider().withGetterPrefix( "" ).withSetterPrefix( "" ) );
    }

    @SneakyThrows
    public Plan plan(@Nullable Resource localState, Resource sourceState, @Nullable Resource cloudState) {
        validate(localState, sourceState, cloudState);

        if (localState != null && cloudState != null) {
            mapper.readerForUpdating(localState).readValue((JsonNode) mapper.valueToTree(cloudState));
        }
        var diff = this.javers.compare(localState, sourceState);
        var changes = javers.processChangeList(diff.getChanges(), new ResourceChangeLog(true));
        localState = handleNullState(localState);
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
    public Plan apply(Plan plan, PluginFactory provider) {
//        Object jsonNode = plan.diffResults();
//        JavaType type = mapper.getTypeFactory().constructFromCanonical(jsonNode);
//        var res = mapper.treeToValue(jsonNode, type);
        for (ChangesByObject diffResult : plan.diffResults()) {
            var textChangeLog = new ResourceChangeLog();
//            for (NewObject newObject : diffResult.getNewObjects()) {
//                textChangeLog.onNewObject(newObject);
//            }
//            for (ObjectRemoved objectRemoved : diffResult.getObjectsRemoved()) {
//                textChangeLog.onObjectRemoved(objectRemoved);
//            }
            for (PropertyChange propertyChange : diffResult.getPropertyChanges()) {
                textChangeLog.onPropertyChange(propertyChange);
            }
            javers.processChangeList(diffResult.get(), new ResourceApplyPlan(textChangeLog, provider));
        }
//        javers.commit("Tudor", plan.sourceCode());
        return plan;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
