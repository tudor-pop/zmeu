package io.zmeu.Config;

import org.flywaydb.core.Flyway;

public class FlywayConf {
    public static void init() {
        var flyway = Flyway.configure()
                .dataSource(Env.ZMEU_DATABASE_URL, Env.ZMEU_DATABASE_USER, Env.ZMEU_DATABASE_PASS)
                .locations("classpath:db/migration") // default location
                .load();

        // Run migrations
        flyway.migrate();
    }
}
