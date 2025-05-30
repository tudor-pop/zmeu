package io.zmeu.Engine;

import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.resource.Resource;

public class ResourceManagerUtils {
    static Resource updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setExisting(resource.getExisting());
        }
        return sourceState;
    }

}
