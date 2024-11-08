package io.zmeu.javers;

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

    public ResourceApplyPlan(AbstractTextChangeLog textChangeLog) {
        this.log = textChangeLog;
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

    @Override
    public void onNewObject(NewObject newObject) {
        log.onNewObject(newObject);
        InstanceId id = (InstanceId) newObject.getAffectedGlobalId();
//        append(io.zmeu.Diff.Change.ADD.coloredOperation() + " resource %s %s { ".formatted(id.getTypeName(), id.getCdoId()));
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        log.onObjectRemoved(objectRemoved);
        var resource = (InstanceId) objectRemoved.getAffectedGlobalId();
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
