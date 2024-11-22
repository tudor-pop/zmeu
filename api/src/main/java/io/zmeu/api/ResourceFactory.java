package io.zmeu.api;

public interface ResourceFactory<T> {
    T create(Resource resource);
}
