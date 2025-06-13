package io.zmeu.Mappers;

import io.zmeu.Resource.Resource;
import io.zmeu.Resource.ResourceType;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ResourceMapper {

    static List<Resource> from(Map<String, Environment<ResourceValue>> schemas) {
        var resources = new ArrayList<Resource>();
        for (var schemaValue : schemas.entrySet()) {
            String schemaName = schemaValue.getKey();
            Environment<ResourceValue> instances = schemaValue.getValue();
            Collection<Resource> from = from(instances.getVariables().values());
            resources.addAll(from);
        }
        return resources;
    }

    @SneakyThrows
    static Resource from(ResourceValue resource) {
        var res = new Resource(resource.getName(), resource.getProperties().getVariables());
        res.setType(new ResourceType(resource.getSchema().getType()));
        res.setDependencies(resource.getDependencies());
        res.setExisting(resource.getExisting());
        return res;
    }

    static Collection<Resource> from(Collection<ResourceValue> resources) {
        return resources.stream().map(ResourceMapper::from).toList();
    }
}
