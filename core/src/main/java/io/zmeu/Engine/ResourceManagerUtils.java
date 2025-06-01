package io.zmeu.Engine;

import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.resource.Resource;
import io.zmeu.api.resource.Identity;

public class ResourceManagerUtils {
    static Resource updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setIdentity(new Identity(resource.getName()));
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setExisting(resource.getExisting());
        }
        return sourceState;
    }

}
