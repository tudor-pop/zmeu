package io.zmeu.Persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.api.resource.Resource;
import jakarta.persistence.NoResultException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(
                    session.createQuery("FROM Resource r WHERE r.identity.name = :name", aClass)
                            .setParameter("name", id)
                            .getSingleResult()
            );
        } catch (NoResultException | NonUniqueResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Resource> findByNameAndType(String name, String type) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(
                    session.createQuery("FROM Resource r WHERE r.identity.name = :name AND r.type=:type", aClass)
                            .setParameter("name", name)
                            .setParameter("type", type)
                            .getSingleResult()
            );
        } catch (NoResultException | NonUniqueResultException e) {
            return Optional.empty();
        }
    }

    public Resource findByProperties(Resource resource) {
        try (Session session = sessionFactory.openSession()) {
            var res = session.createNativeQuery("""
                            SELECT *
                            FROM resources r
                            WHERE r.type = :type
                              AND r.properties @> CAST(:properties AS jsonb)
                            LIMIT 1""", Resource.class)
                    .setParameter("properties", mapper.writeValueAsString(resource.getProperties()))
                    .setParameter("type", resource.getType())
                    .getSingleResultOrNull();
            if (res instanceof Resource resource1) {
                return resource1;
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createQuery("DELETE FROM Resource").executeUpdate();
            tx.commit();
        }
    }
}