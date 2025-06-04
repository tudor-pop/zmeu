package io.zmeu.Plugin;

import io.zmeu.api.Provider;
import io.zmeu.api.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;

@Log4j2
public class ResourceProvider implements ChangeProcessor<Resource> {
    private final Providers providers;
    private Provider provider;
    private Resource resource;
    // Temporary flag to know “this resource has pending updates”
    private boolean dirty = false;

    public ResourceProvider(Providers providers) {
        this.providers = providers;
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {

    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        log.info("onAffectedObject {}", globalId);
    }

    @Override
    public void beforeChangeList() {

    }

    @Override
    public void afterChangeList() {
        if (dirty && provider != null) {
            log.info("Applying batched update for resource {}", resource.getIdentity());
            this.resource = provider.update(resource);
        }
        dirty = false;
    }

    @Override
    public void beforeChange(Change change) {
        if (change.getAffectedObject().isPresent() && change.getAffectedObject().get() instanceof Resource resource) {
            this.resource = resource;
            this.provider = providers.get(resource.getType());
        }
    }

    @Override
    public void afterChange(Change change) {
    }

    @Override
    public void onPropertyChange(PropertyChange change) {
        log.info("onPropertyChange {}", change);
        if (change instanceof InitialValueChange) {// initial value change is same as onNewObject which we already saved
            return;
        }
    }

    @Override
    public void onValueChange(ValueChange change) {
        log.info("onValueChange {}", change);
        if (change instanceof InitialValueChange) {// initial value change is same as onNewObject which we already saved
            return;
        }
        // Mark that this Resource needs an update
        dirty = true;
        log.info("Queued field‐change {} on resource {}", change.getPropertyName(), resource.getIdentity());
    }

    @Override
    public void onReferenceChange(ReferenceChange change) {
        // Mark that this Resource needs an update
        dirty = true;
        log.info("Queued field‐change {} on resource {}", change.getPropertyName(), resource.getIdentity());
    }

    @Override
    public void onNewObject(NewObject newObject) {
        log.info("onNewObject {}", newObject);
        this.resource = provider.create(resource);
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        log.info("onObjectRemoved {}", objectRemoved);
        provider.delete(resource);
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {

    }

    @Override
    public void onSetChange(SetChange setChange) {

    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {

    }

    @Override
    public void onListChange(ListChange listChange) {

    }

    @Override
    public void onMapChange(MapChange mapChange) {

    }

    @Override
    public Resource result() {
        return resource;
    }
}
