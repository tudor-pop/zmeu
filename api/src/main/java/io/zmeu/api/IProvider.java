package io.zmeu.api;

import io.zmeu.api.resource.Resource;
import org.pf4j.ExtensionPoint;

public interface IProvider<T extends Resource> extends ExtensionPoint {
    T create(T resource);

    T read(T resource);

    T update(T resource);

    boolean delete(T resource);

}
