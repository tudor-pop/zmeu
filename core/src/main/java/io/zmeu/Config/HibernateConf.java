package io.zmeu.Config;

import java.util.Properties;

public class HibernateConf {

    public static Properties getProperties() {
        var props = new Properties();
        props.put("hibernate.show_sql", Env.ZMEU_SHOW_SQL);
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.connection.url", Env.ZMEU_DATABASE_URL);
        props.put("hibernate.connection.username", Env.ZMEU_DATABASE_USER);
        props.put("hibernate.connection.password", Env.ZMEU_DATABASE_PASS);
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "validate");
        props.put("hibernate.bytecode.use_reflection_optimizer", "true");
        props.put("hibernate.jdbc.batch_size", Env.ZMEU_JDBC_BATCH_SIZE);
        props.put("hibernate.order_inserts", "true");
        props.put("hibernate.order_updates", "true");
        props.put("hibernate.jdbc.batch_versioned_data", "true");

        return props;
    }

}
