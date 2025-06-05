package io.zmeu.Config;

import java.util.Optional;

public class Env {
    public static String ZMEU_DATABASE_URL = getEnv("ZMEU_DATABASE_URL").orElse("jdbc:postgresql://localhost:5432/postgres");
    public static String ZMEU_DATABASE_USER = getEnv("ZMEU_DATABASE_USER").orElse("postgres");
    public static String ZMEU_DATABASE_PASS = getEnv("ZMEU_DATABASE_PASS").orElse("postgres");
    public static String ZMEU_SHOW_SQL = getEnv("ZMEU_SHOW_SQL").orElse("false");

    public static Optional<String> getEnv(String key) {
        return Optional.ofNullable(System.getenv(key));
    }
}
