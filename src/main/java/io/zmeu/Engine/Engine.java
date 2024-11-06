package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Resource;
import lombok.SneakyThrows;
import org.javers.core.Javers;

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
        var provider = factory.getProviders().get(resource.typeString());
        var className = factory.getPluginManager().getPluginClassLoader("files@0.0.1").loadClass(provider.resourceType());

        var sourceState = (Resource) mapper.convertValue(resource.getProperties().getVariables(), className);
        if (sourceState != null) {
            sourceState.setResourceName(resource.name());
        }
        var localState = (Resource) provider.read(sourceState);

        var cloudStateJavers = javers.getLatestSnapshot(resource.getName(), className).orElse(null);
        var cloudState = mapper.convertValue(cloudStateJavers, className);
        var plan = diff.plan(localState, sourceState, cloudState);
        var res = diff.apply(plan);

        return localState;
    }

}
