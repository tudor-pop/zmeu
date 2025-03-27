package io.zmeu.Dummy;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyProvider extends Provider<DummyResource> {
    private final Map<String, DummyResource> resources = new HashMap<>();

    @Override
    public Resources<DummyResource> resources() {
        return new Resources<>(List.of(new DummyResource()));
    }

    @Override
    public DummyResource create(DummyResource resource) {
        return resources.put(resource.getResourceName(), resource);
    }

    @Override
    public DummyResource read(DummyResource resource) {
        if (resource == null) return null;
        return resources.get(resource.getResourceName());
    }

    @Override
    public DummyResource update(DummyResource resource) {
        return resources.put(resource.getResourceName(), resource);
    }

    @Override
    public boolean delete(DummyResource resource) {
        return resources.remove(resource.getResourceName()) != null;
    }
}
