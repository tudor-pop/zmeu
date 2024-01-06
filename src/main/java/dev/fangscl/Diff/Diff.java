package dev.fangscl.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.fangscl.Backend.Resource;
import dev.fangscl.javers.ShapeChangeLog;
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
        localState = localState == null ? Resource.builder().id("1").build() : localState;
        // overwrite local state with remote state - in memory -
        mapper.readerForUpdating(localState).readValue((JsonNode) mapper.valueToTree(cloudState));
        var diff = this.javers.compare(localState, sourceState);
        var res = javers.processChangeList(diff.getChanges(), new ShapeChangeLog(true));
        return new Plan(mapper.valueToTree(sourceState), mapper.valueToTree(res));
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
