package io.zmeu.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zmeu.api.Resource;
import io.zmeu.javers.ShapeChangeLog;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.javers.core.Javers;
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
    public Plan plan(@Nullable Object localState, Object sourceState, @Nullable Object cloudState) {
        // overwrite local state with remote state - in memory -
//        if (cloudState != null) {
//            cloudState.setCanonicalType(cloudState.getClass().getName());
//        }
        if (cloudState == null) {
            localState = null; // local state is invalid because the cloud resource doesn't exist anymore
        }
        if (localState != null) {
            mapper.readerForUpdating(localState).readValue((JsonNode) mapper.valueToTree(cloudState));
        }
        var diff = this.javers.compare(localState, sourceState);
        var changes = javers.processChangeList(diff.getChanges(), new ShapeChangeLog(true));
        localState = handleNullState(localState);
        return new Plan(sourceState, diff.getChanges());
    }

    private static Object handleNullState(@Nullable Object localState) {
        return localState == null ? Resource.builder().build() : localState;
    }

    @SneakyThrows
    public Plan apply(Plan plan) {
//        Object jsonNode = plan.diffResults();
//        JavaType type = mapper.getTypeFactory().constructFromCanonical(jsonNode);
//        var res = mapper.treeToValue(jsonNode, type);

        javers.commit("Tudor", plan.sourceCode());
        return plan;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
