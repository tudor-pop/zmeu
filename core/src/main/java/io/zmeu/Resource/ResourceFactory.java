package io.zmeu.Resource;

public interface ResourceFactory<T> {
    T create(Resource resource);
}
