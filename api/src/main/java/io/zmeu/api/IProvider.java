package io.zmeu.api;

import org.pf4j.ExtensionPoint;

public interface IProvider<T> extends ExtensionPoint {
    T read(T declaration);

    T create(T resource);

    T update(T resource);

    boolean delete(T resource);

    String namespace();

    String resourceType();
}
