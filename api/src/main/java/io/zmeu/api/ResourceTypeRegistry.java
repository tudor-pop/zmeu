package io.zmeu.api;

import java.util.HashMap;
import java.util.Map;

public class ResourceTypeRegistry {
    private final Map<String, ResourceFactory<?>> factories = new HashMap<>();

    public ResourceTypeRegistry() {
        // Register factories for each resource type
//        factories.put("file", new FileResourceFactory());
//        factories.put("ec2_instance", new EC2InstanceResourceFactory());
        // Add more as needed
    }

    void register(String type, ResourceFactory<?> factory) {
        factories.put(type, factory);
    }

    public <T> T convert(String resourceType, Resource resource) {
        ResourceFactory<?> factory = factories.get(resourceType);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown resource type: " + resourceType);
        }
        return (T) factory.create(resource);
    }
}