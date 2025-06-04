package io.zmeu.Persistence;

import io.zmeu.api.resource.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

public class ResourceRepository extends HibernateRepository<Resource, UUID> {

    public ResourceRepository(SessionFactory sessionFactory) {
        super(Resource.class, sessionFactory);
    }

    public ResourceRepository() {
        super(Resource.class);
    }


    public List<Resource> findByName(String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Resource r WHERE r.identity.name = :name", type)
                    .setParameter("name", id)
                    .getResultList();
        }
    }
    public List<Resource> findByPropertySimilarity(String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Resource r WHERE r.identity.name = :name", type)
                    .setParameter("name", id)
                    .getResultList();
        }
    }
}