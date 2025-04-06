package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.MergeResult;
import io.zmeu.Diff.Plan;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private final HashMap<String, Provider> factory;
    private final ObjectMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();

    public ResourceManager(HashMap<String, Provider> factory, ObjectMapper mapper, Diff diff) {
        this.factory = factory;
        this.mapper = mapper;
        this.diff = diff;
        this.javers = diff.getJavers();
    }

    @SneakyThrows
    public Plan plan(Map<String, Environment<ResourceValue>> schemas) {
        var plan = new Plan();
        for (var schemaValue : schemas.entrySet()) {
            String schemaName = schemaValue.getKey();
            Environment<ResourceValue> instances = schemaValue.getValue();

            for (ResourceValue resourceObject : instances.getVariables().values()) {
                var provider = getProvider(schemaName);

                var mergeResult = plan(provider, resourceObject);
                plan.add(mergeResult);
            }
        }
        return plan;
    }

    @SneakyThrows
    public MergeResult plan(Provider provider, ResourceValue resource) {
        var schema = provider.getSchema(resource.getSchema().getType());
        var o = mapper.convertValue(resource.getProperties().getVariables(), schema);
        var sourceState = new Resource(o);
        updateStateMetadata(resource, sourceState);

        var cloudState = provider.read(sourceState);
        updateStateMetadata(resource, cloudState);

        var snapshot = javers.getLatestSnapshot(resource.getName(), Resource.class).orElse(null);
        if (snapshot == null) {
            return diff.merge(null, sourceState, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, schema);
        updateStateMetadata(resource, javersState);
        return diff.merge(javersState, sourceState, cloudState);
    }

    @SneakyThrows
    public MergeResult plan(Resource srcResource) {
        var schema = srcResource.getResource().getClass();
        var cloudState = getProvider(schema).read(srcResource);
        updateStateMetadata(srcResource, cloudState);

        var snapshot = javers.getLatestSnapshot(srcResource.getResourceName(), srcResource.getClass()).orElse(null);
        if (snapshot == null) {
            return diff.merge(null, srcResource, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, schema);
        updateStateMetadata(srcResource, javersState);
        return diff.merge(javersState, srcResource, cloudState);
    }

    private static void updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setReadOnly(resource.getReadOnly());
        }
    }

    private static void updateStateMetadata(Resource resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getResourceName());
            sourceState.setDependencies(resource.getDependencies());
            sourceState.setReadOnly(resource.getReadOnly());
        }
    }

    public void apply(Plan plan) {
        diff.apply(plan, this.factory);
    }

    public ResourceValue add(ResourceValue resource) {
        return resources.put(resource.name(), resource);
    }

    public Provider getProvider(String schema) {
        return factory.get(schema);
    }

    public Provider getProvider(Class schema) {
        return factory.get(schema.getSimpleName());
    }

}
