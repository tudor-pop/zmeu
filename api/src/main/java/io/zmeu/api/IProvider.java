package io.zmeu.api;

import io.zmeu.api.resource.Resource;
import org.pf4j.ExtensionPoint;

import java.util.UUID;

public interface IProvider extends ExtensionPoint {
    Resource create(Resource resource);

    Resource read(Resource resource);

    Resource update(Resource resource);

    boolean delete(Resource resource);

    default void onNewId(Resource resource){
        resource.setId(UUID.randomUUID());
    }

}
