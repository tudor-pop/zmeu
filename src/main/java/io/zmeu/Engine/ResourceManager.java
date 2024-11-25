package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.Plan;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Plugin.PluginRecord;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Resource;
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
    public Resource plan(Map<String, Environment<ResourceValue>> schemas) {
        for (var schemaValue : schemas.entrySet()) {
            String schemaName = schemaValue.getKey();
            Environment<ResourceValue> instances = schemaValue.getValue();

            PluginRecord pluginRecord = factory.getPluginHashMap().get(schemaName);
            for (ResourceValue resourceObject : instances.getVariables().values()) {
//                if (resourceObject instanceof ResourceValue resourceValue) {
                plan(pluginRecord, resourceObject);
//                }
            }
        }
        return null;
    }

    @SneakyThrows
    private Plan plan(PluginRecord pluginRecord, ResourceValue resource) {
//        var className = pluginRecord.classLoader().loadClass(pluginRecord.provider().resourceType());
        var provider = pluginRecord.provider();

        var className = provider.getSchema(resource.getSchema().getType());
        var sourceState = (Resource) mapper.convertValue(resource.getProperties().getVariables(), className);
        updateStateMetadata(resource, sourceState);

        var cloudState = (Resource) provider.read(sourceState);
        updateStateMetadata(resource, cloudState);

        var snapshot = javers.getLatestSnapshot(resource.getName(), className).orElse(null);
        if (snapshot == null) {
            return diff.plan(null, sourceState, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, className);
        updateStateMetadata(resource, javersState);
        return diff.plan(javersState, sourceState, cloudState);
    }

    private static void updateStateMetadata(ResourceValue resource, Resource sourceState) {
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
            sourceState.setDependencies(resource.getDependencies());
        }
    }

    public ResourceValue add(ResourceValue resource) {
        return resources.put(resource.name(), resource);
    }

}
