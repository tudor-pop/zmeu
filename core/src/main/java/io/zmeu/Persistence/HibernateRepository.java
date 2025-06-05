package io.zmeu.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.function.Consumer;

import static org.hibernate.internal.TransactionManagement.manageTransaction;

public class HibernateRepository<T, ID> implements CrudRepository<T, ID> {
    protected final Class<T> aClass;
    protected final SessionFactory sessionFactory;

    public HibernateRepository(Class<T> aClass, SessionFactory sessionFactory) {
        this.aClass = aClass;
        this.sessionFactory = sessionFactory;
    }

    public HibernateRepository(Class<T> aClass) {
        this.aClass = aClass;
        this.sessionFactory = HibernateUtils.getSessionFactory();
    }

    @Override
    public T findById(ID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(aClass, id);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + aClass.getSimpleName(), aClass).list();
        }
    }

    @Override
    public void save(T entity) {
        inTransaction(session -> session.persist(entity));
    }

    @Override
    public void delete(T entity) {
//            session.inTransaction(tx -> session.remove(entity));
        inTransaction(session -> session.remove(entity));
    }

    void inTransaction(Consumer<Session> action) {
        try (Session session = sessionFactory.openSession()) {
            manageTransaction(session, session.beginTransaction(), session1 -> {
                action.accept(session);
            });
        }
    }
}