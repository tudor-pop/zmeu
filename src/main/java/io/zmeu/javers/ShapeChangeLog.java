package io.zmeu.javers;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.javers.core.changelog.AbstractTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;

import static io.zmeu.Diff.Change.COLORED_OPERATION;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ShapeChangeLog extends AbstractTextChangeLog {
    private String type = COLORED_OPERATION.coloredOperation();
    private Ansi ansi;
    private boolean enableStdout;

    public ShapeChangeLog(boolean enableStdout) {
        this.enableStdout = enableStdout;
    }

    public ShapeChangeLog() {
    }

    @Override
    public void beforeChangeList() {
        ansi = ansi().eraseScreen();
        newLine();
    }

    @Override
    public void afterChangeList() {
        append(type + " }");
        ansi = ansi.render(result());
        if (enableStdout) {
            log.info(ansi);
        }
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        var resource = StringUtils.split(globalId.value(), "/");
        String s = StringUtils.substringAfterLast(resource[0], ".");
        appendln(type + " resource %s %s { ".formatted(s, resource[1]));
    }

    void newLine() {
        ansi.newline();
    }

    @Override
    public void beforeChange(Change change) {
        this.type = switch (change) {
            case InitialValueChange newObject -> io.zmeu.Diff.Change.ADD.coloredOperation();
            case ValueChange valueChange -> COLORED_OPERATION.coloredOperation();
            case ListChange listChange -> COLORED_OPERATION.coloredOperation();
            case MapChange mapChange -> COLORED_OPERATION.coloredOperation();
            case ArrayChange arrayChange -> COLORED_OPERATION.coloredOperation();
            case SetChange setChange -> COLORED_OPERATION.coloredOperation();
            case ReferenceChange referenceChange -> COLORED_OPERATION.coloredOperation();
            case ObjectRemoved objectRemoved -> io.zmeu.Diff.Change.REMOVE.coloredOperation();
            case NewObject newObject -> io.zmeu.Diff.Change.ADD.coloredOperation();
            default -> COLORED_OPERATION.coloredOperation();
        };
    }

    @Override
    public void afterChange(Change change) {
        append("\n");
    }

    public void onValueChange(ValueChange change) {
        switch (change) {
            case InitialValueChange valueChange ->
                    append(type + "\t" + change.getPropertyName() + " = " + change.getRight());
            default ->
                    appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());
        }
    }

    @Override
    public void onReferenceChange(ReferenceChange change) {
        appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onNewObject(NewObject newObject) {
        InstanceId id = (InstanceId) newObject.getAffectedGlobalId();
        append(io.zmeu.Diff.Change.ADD.coloredOperation() + " resource %s %s { ".formatted(id.getTypeName(), id.getCdoId()));
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        var resource = StringUtils.split(objectRemoved.getAffectedGlobalId().value(), "/");
        appendln(io.zmeu.Diff.Change.REMOVE.coloredOperation() + " resource %s %s { ".formatted(resource[0], resource[1]));
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {

    }

    @Override
    public void onSetChange(SetChange change) {
        appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onArrayChange(ArrayChange change) {
        appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onListChange(ListChange change) {
        appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onMapChange(MapChange change) {
        appendln(type + "\t" + change.getPropertyName() + " = " + change.getLeft() + " -> " + change.getRight());

    }


}
