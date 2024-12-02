package io.zmeu.Resources;

import io.zmeu.api.Provider;
import io.zmeu.api.Resources;

import java.util.List;

public class SimpleResourceProvider extends Provider<SimpleResource> {

    @Override
    public Resources<SimpleResource> resources() {
        return new Resources<>(List.of(new SimpleResource()));
    }

    @Override
    public SimpleResource create(SimpleResource resource) {
        return null;
    }

    @Override
    public SimpleResource read(SimpleResource declaration) {
        return null;
    }

    @Override
    public SimpleResource update(SimpleResource resource) {
        return null;
    }

    @Override
    public boolean delete(SimpleResource resource) {
        return false;
    }


}
