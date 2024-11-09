package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Resource;
import lombok.SneakyThrows;
import org.javers.core.Javers;

import java.util.HashMap;

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
    public Resource plan(ResourceValue resource) {
        var pluginRecord = factory.getPluginHashMap().get(resource.typeString());
        var className = pluginRecord.classLoader().loadClass(pluginRecord.provider().resourceType());

        var sourceState = (Resource) mapper.convertValue(resource.getProperties().getVariables(), className);
        if (sourceState != null) {
            sourceState.setResourceName(resource.name());
        }
        var provider = pluginRecord.provider();
        var cloudState = (Resource) provider.read(sourceState);

        var snapshot = javers.getLatestSnapshot(resource.getName(), className);
        if (snapshot.isPresent()) {
            var zmeuState = JaversUtils.mapSnapshotToObject(snapshot.get(), className);
            if (zmeuState instanceof Resource r) {
                r.setResourceName(resource.name());
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
