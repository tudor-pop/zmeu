package io.zmeu.Persistence;

import jakarta.annotation.Nullable;

import java.util.List;

public interface CrudRepository<T, ID> {
    @Nullable
    T findById(ID id);

    @Nullable
    T find(T id);

    List<T> findAll();

    void saveOrUpdate(T entity);

    void delete(T entity);
}
