package io.zmeu.Engine;

import io.zmeu.Runtime.Values.ResourceValue;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private final List<ResourceValue> resources;

    public Engine() {
        this.resources = new ArrayList<>();
    }

    public void process(ResourceValue instance) {
        resources.add(instance);
    }

    public List<ResourceValue> getResources() {
        return resources;
    }

}
