package io.zmeu.api.resource;

public interface ResourceFactory<T> {
    T create(Resource resource);
}
