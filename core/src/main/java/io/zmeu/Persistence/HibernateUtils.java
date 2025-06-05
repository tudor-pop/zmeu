package io.zmeu.Persistence;

import io.zmeu.Config.HibernateConf;
import io.zmeu.api.resource.Resource;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            var configuration = new Configuration()
                    .addProperties(HibernateConf.getProperties())
                    .addAnnotatedClass(Resource.class);

            var registry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());

            sessionFactory = configuration.buildSessionFactory(registry.build());
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

}
