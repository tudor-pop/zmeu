package io.zmeu.Dummy;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class DummyProvider extends Provider {
    private final Map<String, Resource> resources = new HashMap<>();

    @Override
    public Object initResource() {
        return new DummyResource();
    }

    @Override
    public Resource create(Resource resource) {
        return resources.put(resource.getId(), resource);
    }

    @Override
    public Resource read(Resource resource) {
        if (resource == null) return null;
        return resources.get(resource.getId());
    }

    @Override
    public Resource update(Resource resource) {
        return resources.put(resource.getId(), resource);
    }

    @Override
    public boolean delete(Resource resource) {
        return resources.remove(resource.getId()) != null;
    }
}
