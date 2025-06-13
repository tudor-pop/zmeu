package io.zmeu.Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.MergeResult;
import io.zmeu.Diff.Plan;
import io.zmeu.Persistence.ResourceRepository;
import io.zmeu.Plugin.CloudProcessor;
import io.zmeu.Plugin.Providers;
import io.zmeu.Resource.Resource;
import io.zmeu.Resource.ResourceFactory;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.api.Provider;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
public class ResourceManager {
    private final Providers providers;
    private final ObjectMapper mapper;
    private final Diff diff;
    private final Javers javers;
    private final HashMap<String, ResourceValue> resources = new HashMap<>();
    private final ResourceChangeLog changeLog;
    private final CloudProcessor cloudProcessor;
    private final ResourceRepository repository;

    public ResourceManager(Providers providers, ObjectMapper mapper, Diff diff, ResourceRepository repository) {
        this.providers = providers;
        this.mapper = mapper;
        this.diff = diff;
        this.javers = diff.getJavers();
        this.changeLog = new ResourceChangeLog(ObjectMapperConf.getObjectMapper());
        this.cloudProcessor = new CloudProcessor(providers);
        this.repository = repository;
    }

    @SneakyThrows
    public Plan plan(Collection<Resource> resources) {
        var plan = new Plan();

        for (var resource : resources) {
            var result = plan(resource);
            plan.add(result);
        }

        return plan;
    }

    @SneakyThrows
    public MergeResult plan(Resource src) {
        var provider = getProvider(src.getKind());
        var schema = provider.getSchema(src.getKind());

        var localState = find(src);

        // cloud resource can only be read from local state if the local state contains an ARN or some id/name
        // otherwise it's a new resource and we can't read it from the cloud
        // => resources are only stored in db after they've been created in the cloud
        Resource cloudState = null;
        if (localState != null) {
            var cloudResourceProperties = provider.read(localState.getProperties());
            cloudState = ResourceFactory.from(src, cloudResourceProperties);
        }

        var merged = diff.merge(localState, src, cloudState);

        return merged;
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
            javers.processChangeList(changes1, cloudProcessor);
            Resource result = cloudProcessor.result();
            repository.saveOrUpdate(result);
        }
        return plan;
    }

    public ResourceValue add(ResourceValue resource) {
        return resources.put(resource.name(), resource);
    }

    public Provider getProvider(String schema) {
        return providers.get(schema);
    }

    public Provider getProvider(Class schema) {
        return providers.get(schema.getSimpleName());
    }

    @Nullable
    public Resource find(Resource resource) {
        resource = repository.find(resource);
        if (resource == null) return null;
        resource.setProperties(mapper.convertValue(resource.getProperties(), getSchema(resource)));
        return resource;
    }

    private Class<?> getSchema(Resource state) {
        var type = state.getKind();
        return getProvider(type)
                .getSchema(type);
    }

    public String changelog() {
        return changeLog.result();
    }

}
