package io.zmeu.Persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.api.resource.Resource;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaUpdate;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ResourceRepository extends HibernateRepository<Resource, UUID> {
    protected ObjectMapper mapper = ObjectMapperConf.getObjectMapper();

    public ResourceRepository(SessionFactory sessionFactory) {
        super(Resource.class, sessionFactory);
    }

    public ResourceRepository() {
        super(Resource.class);
    }

    public Optional<Resource> findByName(String id) {
        return Optional.ofNullable(factory.fromTransaction(session ->
                session.createQuery("FROM Resource r WHERE r.identity.name = :name", aClass)
                        .setParameter("name", id)
                        .getSingleResultOrNull()
        ));
    }

    public Optional<Resource> findByNameAndType(String name, String type) {
        return Optional.ofNullable(factory.fromTransaction(session ->
                session.createQuery("FROM Resource r WHERE r.identity.name = :name AND r.type=:type", aClass)
                        .setParameter("name", name)
                        .setParameter("type", type)
                        .getSingleResultOrNull()
        ));
    }

    @Override
    public Resource find(Resource resource) {
        return factory.fromTransaction(session -> {
            try {
                return session.createNativeQuery("""
                                SELECT *
                                FROM resources r
                                WHERE (r.type = :type AND r.name = :name)
                                   OR (r.type = :type AND r.properties @> CAST(:properties AS jsonb))
                                LIMIT 1
                                """, Resource.class)
                        .setParameter("properties", mapper.writeValueAsString(resource.getProperties()))
                        .setParameter("type", resource.getType())
                        .setParameter("name", resource.getResourceNameString())
                        .getSingleResultOrNull();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void saveOrUpdate(Resource entity) {
        factory.inTransaction(session -> {
            if (entity.getId() == null) {
                // No ID set → must be new
                session.persist(entity);
            } else if (session.contains(entity)) {
                // Already managed → nothing to do
            } else {
                // Not managed, has ID → try update
                Object persistent = session.get(entity.getClass(), entity.getId());
                if (persistent == null) {
                    // No such row → insert
                    session.persist(entity);
                } else {
                    // Exists → update
                    session.merge(entity);
                }
            }
        });
    }

    public void deleteAll() {
        factory.inTransaction(session -> session.createQuery("DELETE FROM Resource").executeUpdate());
    }
}