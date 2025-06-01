package io.zmeu.api;

import io.zmeu.api.resource.Resource;
import org.pf4j.ExtensionPoint;

public interface IProvider extends ExtensionPoint {
    Resource create(Resource resource);

    Resource read(Resource resource);

    Resource update(Resource resource);

    boolean delete(Resource resource);

    void setId(Resource resource);
}
