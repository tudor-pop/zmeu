package io.zmeu.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zmeu.Backend.Resource;
import io.zmeu.javers.ShapeChangeLog;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;

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
    public Diff(String connection, String username, String password) {
        this();
        var dbConnection = DriverManager.getConnection(connection, username, password);

        var connectionProvider = new ConnectionProvider() {
            @Override
            public Connection getConnection() {
                //suitable only for testing!
                return dbConnection;
            }
        };
        var sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.POSTGRES)
                .withCommitTableName("shape_commit")
                .withGlobalIdTableName("shape_global_id")
                .withSnapshotTableName("shape_snapshot")
                .withCommitPropertyTableName("shape_commit_property")
                .build();

        //given
        javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .registerJaversRepository(sqlRepository)
                .withPrettyPrint(true)
                .build();
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
        localState = handleNullState(localState);
        // overwrite local state with remote state - in memory -
        cloudState.setCanonicalType(cloudState.getClass().getName());
        mapper.readerForUpdating(localState).readValue((JsonNode) mapper.valueToTree(cloudState));
        var diff = this.javers.compare(localState, sourceState);
        var changes = javers.processChangeList(diff.getChanges(), new ShapeChangeLog(true));

        return new Plan(mapper.valueToTree(sourceState), mapper.valueToTree(localState));
    }

    private static Resource handleNullState(@Nullable Resource localState) {
        return localState == null ? Resource.builder().id("1").build() : localState;
    }

    @SneakyThrows
    public Plan apply(@Nullable Resource localState, Resource sourceState, @Nullable Resource cloudState) {
        var plan = plan(localState, sourceState, cloudState);

        JsonNode jsonNode = plan.diffResults();
        JavaType type = mapper.getTypeFactory().constructFromCanonical(jsonNode.path("canonicalType").asText());
        var res = mapper.treeToValue(jsonNode, type);

        javers.commit("Tudor", res);
        return plan;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
