package io.zmeu.Engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Resource;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;

public class Engine {
    private final PluginFactory factory;
    private final YAMLMapper mapper;
    private final Diff diff;
    private final Javers javers;

    public Engine(PluginFactory factory, YAMLMapper mapper, Diff diff, Javers javers) {
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
        var localState = (Resource) provider.read(sourceState);

        var snapshot = javers.getLatestSnapshot(resource.getName(), className);
        if (snapshot.isPresent()) {
            var cloudState = JaversUtils.mapSnapshotToObject(snapshot.get(), className);
            var plan = diff.plan(localState, sourceState, cloudState);
            var res = diff.apply(plan);
        } else {
            var plan = diff.plan(localState, sourceState, null);
            var res = diff.apply(plan);
        }

        return localState;
    }



}
