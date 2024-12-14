package io.zmeu.Dummy;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resources;

import java.util.List;

public class DummyProvider extends Provider<DummyResource> {
    @Override
    public Resources<DummyResource> resources() {
        return new Resources<>(List.of(new DummyResource()));
    }

    @Override
    public DummyResource create(DummyResource resource) {
        return null;
    }

    @Override
    public DummyResource read(DummyResource resource) {
        return null;
    }

    @Override
    public DummyResource update(DummyResource resource) {
        return null;
    }

    @Override
    public boolean delete(DummyResource resource) {
        return false;
    }
}
