package io.zmeu.Persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.Resource.Resource;
import org.hibernate.SessionFactory;
import org.postgresql.util.PGobject;

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
                                SELECT res.*
                                FROM resources res
                                JOIN identity i ON res.identity_id = i.id
                                JOIn resource_type type ON res.resource_type_id = type.id
                                WHERE (type.kind = :type AND i.name = :name)
                                   OR (type.kind = :type AND res.properties @> CAST(:properties AS jsonb))
                                LIMIT 1
                                """, Resource.class)
                        .setParameter("properties", mapper.writeValueAsString(resource.getProperties()), JsonBinaryType.INSTANCE)
                        .setParameter("type", resource.getType().getKind())
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