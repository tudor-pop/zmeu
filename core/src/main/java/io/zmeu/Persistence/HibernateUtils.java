package io.zmeu.Persistence;

import io.zmeu.Config.HibernateConf;
import io.zmeu.Resource.Identity;
import io.zmeu.Resource.Resource;
import io.zmeu.Resource.ResourceType;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public final class HibernateUtils {
    private static SessionFactory sessionFactory;

    private HibernateUtils() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) return sessionFactory;
        try {
            var configuration = new Configuration()
                    .addProperties(HibernateConf.getProperties())
                    .addAnnotatedClass(Resource.class)
                    .addAnnotatedClass(ResourceType.class)
                    .addAnnotatedClass(Identity.class);

            var registry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(registry.build());
            return sessionFactory;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

}
