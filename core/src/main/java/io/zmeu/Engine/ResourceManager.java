package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.MergeResult;
import io.zmeu.Diff.Plan;
import io.zmeu.Plugin.Providers;
import io.zmeu.Plugin.ResourceProvider;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.api.resource.Identity;
import io.zmeu.api.resource.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static io.zmeu.Engine.ResourceManagerUtils.updateStateMetadata;

public class ResourceManager {
    private final Providers factory;
    private final ObjectMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();
    private final ResourceChangeLog changeLog;
    private final ResourceProvider resourceProvider;

    public ResourceManager(Providers factory, ObjectMapper mapper, Diff diff) {
        this.factory = factory;
        this.mapper = mapper;
        this.diff = diff;
        this.javers = diff.getJavers();
        this.changeLog = new ResourceChangeLog();
        this.resourceProvider = new ResourceProvider(factory);
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
        var srcResource = mapper.convertValue(resource.getProperties().getVariables(), schema);
        var sourceState = new Resource(resource.getName(), srcResource);
        updateStateMetadata(resource, sourceState);

        return plan(sourceState);
    }

    @SneakyThrows
    public MergeResult plan(Resource src) {
        var provider = getProvider(src.getType());
        var schema = provider.getSchema(src.getType());

        var cloudState = provider.read(src);

//        var snapshot = javers.getLatestSnapshot(src.getResourceName(), src.getClass()).orElse(null);
        var localState = findByResourceName(src.getIdentity());
        if (localState == null) {
            return diff.merge(null, src, cloudState);
        }
        return diff.merge(localState, src, cloudState);
    }

    public Plan toPlan(MergeResult src) {
        Plan plan = new Plan();
        plan.add(src);
        return plan;
    }

    /**
     * Calls the provider CRUD methods and saves the state into javers
     */
    @SneakyThrows
    public Plan apply(Plan plan) {
        for (MergeResult mergeResult : plan.getMergeResults()) {
            Changes changes1 = mergeResult.changes();
            javers.processChangeList(changes1, changeLog);
            javers.processChangeList(changes1, resourceProvider);
            javers.commit("Tudor", resourceProvider.result());
        }
        return plan;
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

    @Nullable
    public Resource findByResourceName(Identity identity) {
        var shadowList = javers.<Resource>findShadows(QueryBuilder.byInstanceId(identity, Resource.class).limit(1).build());
        if (shadowList.isEmpty()) {
            return null;
        }
        Shadow<Resource> shadow = shadowList.get(0);
        var resource = shadow.get();
        if (shadow.getCdoSnapshot().getGlobalId() instanceof InstanceId id) {
            if (id.getCdoId() instanceof Identity identity1) { // set state identity which has the stable id from state
                resource.setIdentity(identity1);
            } else { // or store the identity from source code (could not have any stable id)
                resource.setIdentity(identity);
            }
        }
        resource.setResource(mapper.convertValue(resource.getResource(), getSchema(resource)));
        return resource;
    }

    private Class<?> getSchema(Resource state) {
        return getProvider(state.getType())
                .getSchema(state.getType());
    }

}
