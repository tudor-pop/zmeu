package io.zmeu.api;

import org.pf4j.ExtensionPoint;

public interface IProvider<T> extends ExtensionPoint {
    T create(T resource);

    T read(T declaration);

    T update(T resource);

    boolean delete(T resource);

}
