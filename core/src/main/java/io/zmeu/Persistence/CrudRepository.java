package io.zmeu.Persistence;

import java.util.List;

public interface CrudRepository<T, ID> {
    T findById(ID id);

    List<T> findAll();

    void save(T entity);

    void delete(T entity);
}
