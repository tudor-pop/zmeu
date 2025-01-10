package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.MergeResult;
import io.zmeu.Diff.Plan;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
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
    public Plan merge(Map<String, Environment<ResourceValue>> schemas) {
        var plan = new Plan();
        for (var schemaValue : schemas.entrySet()) {
            String schemaName = schemaValue.getKey();
            Environment<ResourceValue> instances = schemaValue.getValue();

            for (ResourceValue resourceObject : instances.getVariables().values()) {
                var provider = getProvider(schemaName);

                var className = provider.getSchema(resourceObject.getSchema().getType());
                var mergeResult = merge(provider, resourceObject, className);
                plan.add(mergeResult);
            }
        }
        return plan;
    }

    @SneakyThrows
    public MergeResult merge(Provider provider, ResourceValue resource, Class schema) {
        var sourceState = (Resource) mapper.convertValue(resource.getProperties().getVariables(), schema);
        updateStateMetadata(resource, sourceState);

        var cloudState = provider.read(sourceState);
        updateStateMetadata(resource, cloudState);

        var snapshot = javers.getLatestSnapshot(resource.getName(), schema).orElse(null);
        if (snapshot == null) {
            return diff.merge(null, sourceState, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, schema);
        updateStateMetadata(resource, javersState);
        return diff.merge(javersState, sourceState, cloudState);
    }

    @SneakyThrows
    public MergeResult merge(@NonNull List<Resource> srcResource, Class schema) {

    }

    public MergeResult merge(@Nullable Resource srcResource, Class schema) {
        var cloudState = getProvider(schema).read(srcResource);
        updateStateMetadata(srcResource, cloudState);

        var snapshot = javers.getLatestSnapshot(srcResource.getResourceName(), schema).orElse(null);
        if (snapshot == null) {
            return diff.merge(null, srcResource, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, schema);
        updateStateMetadata(srcResource, javersState);
        return diff.merge(javersState, srcResource, cloudState);
    }

    @SneakyThrows
    public Plan plan(@Nullable Resource srcResource, Class schema) {
        var merge = merge(srcResource, schema);
        return new Plan(merge);
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
