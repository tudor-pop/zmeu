package io.zmeu.Config;

import java.util.Properties;

public class HibernateConf {

    public static Properties getProperties() {
        var props = new Properties();
        props.put("hibernate.show_sql", Env.ZMEU_SHOW_SQL);
        props.put("hibernate.connection.url", Env.ZMEU_DATABASE_URL);
        props.put("hibernate.connection.username", Env.ZMEU_DATABASE_USER);
        props.put("hibernate.connection.password", Env.ZMEU_DATABASE_PASS);
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "validate");
        props.put("hibernate.bytecode.use_reflection_optimizer", "true");
        return props;
    }

}
