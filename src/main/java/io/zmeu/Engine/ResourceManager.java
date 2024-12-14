package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.MergeResult;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.Plan;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Plugin.PluginRecord;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private final PluginFactory factory;
    private final ObjectMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();

    public ResourceManager(PluginFactory factory, ObjectMapper mapper, Diff diff, Javers javers) {
        this.factory = factory;
        this.mapper = mapper;
        this.diff = diff;
        this.javers = javers;
    }

    @SneakyThrows
    public Plan plan(Map<String, Environment<ResourceValue>> schemas) {
        var plan = new Plan();
        for (var schemaValue : schemas.entrySet()) {
            String schemaName = schemaValue.getKey();
            Environment<ResourceValue> instances = schemaValue.getValue();

            PluginRecord pluginRecord = factory.getPluginHashMap().get(schemaName);
            for (ResourceValue resourceObject : instances.getVariables().values()) {
                var provider = pluginRecord.provider();

                var className = provider.getSchema(resourceObject.getSchema().getType());
                var mergeResult = plan(provider, resourceObject, className);
                plan.add(mergeResult);
            }
        }
        return plan;
    }

    @SneakyThrows
    private MergeResult plan(Provider provider, ResourceValue resource, Class schema) {
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

    private static void updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
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

}
