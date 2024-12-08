package io.zmeu.Resources;

import io.zmeu.api.Provider;
import io.zmeu.api.Resources;

import java.util.List;

public class TestResourceProvider extends Provider<TestResource> {

    @Override
    public Resources<TestResource> resources() {
        return new Resources<>(List.of(new TestResource()));
    }

    @Override
    public TestResource create(TestResource resource) {
        return null;
    }

    @Override
    public TestResource read(TestResource declaration) {
        return null;
    }

    @Override
    public TestResource update(TestResource resource) {
        return null;
    }

    @Override
    public boolean delete(TestResource resource) {
        return false;
    }


}
