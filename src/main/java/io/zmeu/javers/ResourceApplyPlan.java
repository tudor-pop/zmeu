package io.zmeu.javers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.api.Provider;
import lombok.SneakyThrows;
import org.javers.core.changelog.AbstractTextChangeLog;
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
import org.javers.core.metamodel.object.InstanceId;

import static org.fusesource.jansi.Ansi.ansi;

public class ResourceApplyPlan implements ChangeProcessor<String> {
    private final AbstractTextChangeLog log;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PluginFactory factory;

    public ResourceApplyPlan(AbstractTextChangeLog textChangeLog,  PluginFactory factory) {
        this.log = textChangeLog;
        this.factory = factory;
    }


    @Override
    public void beforeChangeList() {
        log.beforeChangeList();
    }

    @Override
    public void afterChangeList() {
        log.afterChangeList();
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        log.onCommit(commitMetadata);
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        log.onAffectedObject(globalId);
    }

    @Override
    public void beforeChange(Change change) {
        log.beforeChange(change);

    }

    @Override
    public void afterChange(Change change) {
        log.afterChange(change);
    }

    @Override
    public void onPropertyChange(PropertyChange propertyChange) {
        log.onPropertyChange(propertyChange);
    }

    public void onValueChange(ValueChange change) {
        log.onValueChange(change);
    }


    @Override
    public void onReferenceChange(ReferenceChange change) {
        log.onReferenceChange(change);
    }

    @SneakyThrows
    @Override
    public void onNewObject(NewObject object) {
        log.onNewObject(object);
        InstanceId id = (InstanceId) object.getAffectedGlobalId();
        if (object.getAffectedObject().isPresent()) {
            String typeName = object.getAffectedGlobalId().getTypeName();
            var pluginRecord = factory.getPluginHashMap().get(typeName);

            var className = pluginRecord.classLoader().loadClass(pluginRecord.provider().resourceType());
            var resource = objectMapper.convertValue(object.getAffectedObject().get(), className);
            var provider = pluginRecord.provider();
            provider.create(resource);

        }
//        append(io.zmeu.Diff.Change.ADD.coloredOperation() + " resource %s %s { ".formatted(id.getTypeName(), id.getCdoId()));
    }

    @SneakyThrows
    @Override
    public void onObjectRemoved(ObjectRemoved object) {
        log.onObjectRemoved(object);
        InstanceId id = (InstanceId) object.getAffectedGlobalId();
        if (object.getAffectedObject().isPresent()) {
            String typeName = object.getAffectedGlobalId().getTypeName();
            var pluginRecord = factory.getPluginHashMap().get(typeName);

            var className = pluginRecord.classLoader().loadClass(pluginRecord.provider().resourceType());
            var resource = objectMapper.convertValue(object.getAffectedObject().get(), className);
            var provider = pluginRecord.provider();
            provider.remove(resource);

        }
//        appendln(io.zmeu.Diff.Change.REMOVE.coloredOperation() + " resource %s %s { ".formatted(resource.getTypeName(), resource.getCdoId()));
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {
        log.onContainerChange(containerChange);
    }

    @Override
    public void onSetChange(SetChange change) {
        log.onSetChange(change);
        //        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onArrayChange(ArrayChange change) {
        log.onArrayChange(change);
//        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onListChange(ListChange change) {
        log.onListChange(change);
//        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onMapChange(MapChange change) {
        log.onMapChange(change);
//        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public String result() {
        return log.result();
    }


}