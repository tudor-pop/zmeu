package io.zmeu.Persistence;

import java.util.Optional;
import java.util.Properties;

public class HibernateConf {

    public static Properties getProperties() {
        var props = new Properties();
        getEnv("ZMEU_DATABASE_URL").ifPresent(it -> props.put("hibernate.connection.url", it));
        getEnv("ZMEU_DATABASE_USER").ifPresent(it -> props.put("hibernate.connection.username", it));
        getEnv("ZMEU_DATABASE_PASS").ifPresent(it -> props.put("hibernate.connection.password", it));
        getEnv("ZMEU_SHOW_SQL").ifPresent(it -> props.put("hibernate.show_sql", it));
        return props;
    }

    private static Optional<String> getEnv(String key) {
        return Optional.ofNullable(System.getenv(key));
    }
}
