package io.zmeu.Dummy;

import io.zmeu.api.Provider;

import java.util.HashMap;
import java.util.Map;

public class DummyProvider extends Provider<DummyResource> {
    /**
     * simulate as this state represent resources in the cloud
     */
    private final Map<String, DummyResource> resources = new HashMap<>();

    @Override
    public DummyResource initResource() {
        return new DummyResource();
    }

    @Override
    public DummyResource create(DummyResource resource) {
        resource.setArn("arn:%s".formatted(resources.size() + 1));
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
