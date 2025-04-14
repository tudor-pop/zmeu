package io.zmeu.Engine;

import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.resource.Resource;

public class ResourceManagerUtils {
    static Resource updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setReadOnly(resource.getReadOnly());
        }
        return sourceState;
    }

    static void updateStateMetadata(Resource resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getResourceName());
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setReadOnly(resource.getReadOnly());
        }
    }
}
