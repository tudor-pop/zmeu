package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Plugin.PluginRecord;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.api.Resource;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.util.HashMap;
import java.util.List;

public class ResourceManager {
    private final PluginFactory factory;
    private final YAMLMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();

    public ResourceManager(PluginFactory factory, YAMLMapper mapper, Diff diff, Javers javers) {
        this.factory = factory;
        this.mapper = mapper;
        this.diff = diff;
        this.javers = javers;
    }

    @SneakyThrows
    public Resource plan(List<SchemaValue> resource) {
        for (SchemaValue schemaValue : resource) {
            PluginRecord pluginRecord = factory.getPluginHashMap().get(schemaValue.typeString());
            for (var resourceObject : schemaValue.getInstances().getVariables().values()) {
                if (resourceObject instanceof ResourceValue resourceValue) {
                    plan(pluginRecord, resourceValue);
                }
            }
        }
        return null;
    }

    @SneakyThrows
    private Resource plan(PluginRecord pluginRecord, ResourceValue resource) {
//        var className = pluginRecord.classLoader().loadClass(pluginRecord.provider().resourceType());
        var provider = pluginRecord.provider();

        var className = provider.getSchema(resource.getSchema().typeString());
        var sourceState = (Resource) mapper.convertValue(resource.getProperties().getVariables(), className);
        if (sourceState != null) {
            sourceState.setResourceName(resource.getName());
        }

        var cloudState = (Resource) provider.read(sourceState);
        if (cloudState != null) {
            cloudState.setResourceName(resource.getName());
        }

        var snapshot = javers.getLatestSnapshot(resource.getName(), className);
        if (snapshot.isPresent()) {
            var zmeuState = (Resource) JaversUtils.mapSnapshotToObject(snapshot.get(), className);
            if (zmeuState != null) {
                zmeuState.setResourceName(resource.name());
            }
            var plan = diff.plan(zmeuState, sourceState, cloudState);
            var res = diff.apply(plan, factory);
        } else {
            var plan = diff.plan(null, sourceState, cloudState);
            var res = diff.apply(plan, factory);
        }

        return cloudState;
    }

    public ResourceValue add(ResourceValue resource) {
        return resources.put(resource.name(), resource);
    }

}
