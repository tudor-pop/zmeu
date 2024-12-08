package io.zmeu.Diff;

import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;

import java.sql.Connection;
import java.sql.DriverManager;

public class JaversFactory {
    @SneakyThrows
    public static Javers create(String connection, String username, String password) {
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
                .withCommitTableName("zmeu_commit")
                .withGlobalIdTableName("zmeu_global_id")
                .withSnapshotTableName("zmeu_snapshot")
                .withCommitPropertyTableName("zmeu_commit_property")
                .build();

        //given
        return getJavers(sqlRepository);
    }

    @SneakyThrows
    public static Javers createH2() {
        var dbConnection = DriverManager.getConnection("jdbc:h2:mem:test");
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
                .withDialect(DialectName.H2)
                .withCommitTableName("zmeu_commit")
                .withGlobalIdTableName("zmeu_global_id")
                .withSnapshotTableName("zmeu_snapshot")
                .withCommitPropertyTableName("zmeu_commit_property")
                .build();

        //given
        return getJavers(sqlRepository);
    }

    private static Javers getJavers(JaversSqlRepository sqlRepository) {
        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .registerJaversRepository(sqlRepository)
                .withPrettyPrint(true)
                .build();
    }
}
