package io.zmeu.Dummy;

import io.zmeu.api.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DummyProvider extends Provider<DummyResource> {
    private final Map<String, DummyResource> resources = new HashMap<>();

    @Override
    public DummyResource initResource() {
        return new DummyResource();
    }

    @Override
    public DummyResource create(DummyResource resource) {
        resource.setArn(UUID.randomUUID().toString());
        resources.put(resource.getArn(), resource);
        return resources.get(resource.getArn());
    }

    @Override
    public DummyResource read(DummyResource resource) {
        if (resource == null) return null;
        return resources.get(resource.getArn());
    }

    @Override
    public DummyResource update(DummyResource resource) {
        return resources.put(resource.getArn(), resource);
    }

    @Override
    public boolean delete(DummyResource resource) {
        return resources.remove(resource.getArn()) != null;
    }
}
