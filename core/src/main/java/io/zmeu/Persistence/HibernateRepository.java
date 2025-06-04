package io.zmeu.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.function.Consumer;

import static org.hibernate.internal.TransactionManagement.manageTransaction;

public class HibernateRepository<T, ID> implements CrudRepository<T, ID> {
    private final Class<T> type;
    private final SessionFactory sessionFactory;

    public HibernateRepository(Class<T> type, SessionFactory sessionFactory) {
        this.type = type;
        this.sessionFactory = sessionFactory;
    }

    public HibernateRepository(Class<T> type) {
        this.type = type;
        this.sessionFactory = HibernateUtils.getSessionFactory();
    }

    @Override
    public T findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(type, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + type.getName(), type).list();
        }
    }

    @Override
    public void save(T entity) {
        inTransaction(session -> session.persist(entity));
    }

    @Override
    public void delete(T entity) {
//            session.inTransaction(tx -> session.remove(entity));
        inTransaction(session ->session.remove(entity));
    }

    void inTransaction(Consumer<Session> action) {
        try (Session session = sessionFactory.openSession()) {
            manageTransaction(session, session.beginTransaction(), session1 -> {
                action.accept(session);
            });
        }
    }
}