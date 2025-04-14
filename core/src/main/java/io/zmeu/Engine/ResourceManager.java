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
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.zmeu.Engine.ResourceManagerUtils.updateStateMetadata;

public class ResourceManager {
    private final HashMap<String, Provider> factory;
    private final ModelMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();

    public ResourceManager(HashMap<String, Provider> factory, ModelMapper mapper, Diff diff) {
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
    private MergeResult plan(Provider provider, ResourceValue resource) {
        var schema = provider.getSchema(resource.getSchema().getType());
        var srcResource = mapper.map(resource.getProperties().getVariables(), schema);
        var sourceState = new Resource(srcResource);
        updateStateMetadata(resource, sourceState);

        return plan(sourceState);
    }

    @SneakyThrows
    public MergeResult plan(Resource src) {
        Provider provider = getProvider(src.getType());

        var cloudState = provider.read(src);
        updateStateMetadata(src, cloudState);

        var snapshot = javers.getLatestSnapshot(src.getResourceName(), src.getClass()).orElse(null);
        if (snapshot == null) {
            return diff.merge(null, src, cloudState);
        }

        var javersState = (Resource) JaversUtils.mapSnapshotToObject(snapshot, src.getResourceClass());
        updateStateMetadata(src, javersState);
        return diff.merge(javersState, src, cloudState);
    }

    public Plan toPlan(MergeResult src) {
        Plan plan = new Plan();
        plan.add(src);
        return plan;
    }

    public Plan apply(Plan plan) {
        return diff.apply(plan, this.factory);
    }

    public Plan apply(Map<String, Environment<ResourceValue>> schemas) {
        return apply(plan(schemas));
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

    public Object findByResourceName(String resourceName) {
        var snapshot = javers.getLatestSnapshot(resourceName, Resource.class);
        if (snapshot.isEmpty()) {
            return null;
        }
        var state = JaversUtils.mapSnapshotToObject(snapshot.get(), Resource.class);
        state.setResource(mapper.map(state.getResource(), getSchema(state)));

        return state;
    }

    private Class<?> getSchema(Resource state) {
        return getProvider(state.getType())
                .getSchema(state.getType());
    }

}
