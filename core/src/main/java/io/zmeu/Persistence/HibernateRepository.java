package io.zmeu.Persistence;

import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateRepository<T, ID> implements CrudRepository<T, ID> {
    protected final Class<T> aClass;
    protected final SessionFactory factory;

    public HibernateRepository(Class<T> aClass, SessionFactory factory) {
        this.aClass = aClass;
        this.factory = factory;
    }

    public HibernateRepository(Class<T> aClass) {
        this.aClass = aClass;
        this.factory = HibernateUtils.getSessionFactory();
    }

    @Override
    public T findById(ID id) {
        return factory.fromTransaction(session -> session.get(aClass, id));
    }

    @Override
    public T find(T object) {
        return factory.fromTransaction(session -> session.find(aClass, object));
    }

    @Override
    public List<T> findAll() {
        return factory.fromTransaction(session -> session.createSelectionQuery("FROM " + aClass.getSimpleName(), aClass).list());
    }

    @Override
    public void saveOrUpdate(T entity) {
        factory.inTransaction(session -> session.merge(entity));
    }

    @Override
    public void delete(T entity) {
        factory.inTransaction(session -> session.remove(entity));
    }

}