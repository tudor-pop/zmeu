package io.zmeu.Dummy;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import io.zmeu.api.resource.Resources;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyProvider extends Provider {
    private final Map<String, Resource> resources = new HashMap<>();

    @Override
    public Object initResource() {
        return new DummyResource();
    }

    @Override
    public Resource create(Resource resource) {
        return resources.put(resource.getResourceName(), resource);
    }

    @Override
    public Resource read(Resource resource) {
        if (resource == null) return null;
        return resources.get(resource.getResourceName());
    }

    @Override
    public Resource update(Resource resource) {
        return resources.put(resource.getResourceName(), resource);
    }

    @Override
    public boolean delete(Resource resource) {
        return resources.remove(resource.getResourceName()) != null;
    }
}
