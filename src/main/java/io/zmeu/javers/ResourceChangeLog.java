package io.zmeu.javers;

import io.zmeu.Diff.ResourceChange;
import lombok.extern.log4j.Log4j2;
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

import static io.zmeu.Diff.ResourceChange.*;
import static io.zmeu.Diff.ResourceChange.CHANGE;
import static io.zmeu.Diff.ResourceChange.NO_OP;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ResourceChangeLog extends AbstractTextChangeLog {
    private ResourceChange type = NO_OP;
    private Ansi ansi;
    private boolean enableStdout;
    private InstanceId globalId;
    private static final String EQUALS = "\t= ";

    public ResourceChangeLog(boolean enableStdout) {
        this.enableStdout = enableStdout;
    }

    public ResourceChangeLog() {
    }

    @Override
    public void beforeChangeList() {
        ansi = ansi().eraseScreen();
        newLine();
    }

    @Override
    public void afterChangeList() {
        if (type != NO_OP) {
            append(type.coloredOperation() + " }");
        }
        ansi = ansi.render(result());
        if (enableStdout) {
            log.info(ansi);
        }
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        this.globalId = (InstanceId) globalId;
        appendln(type.coloredOperation() + " resource %s %s { ".formatted(this.globalId.getTypeName(), this.globalId.getCdoId()));
    }

    @Override
    public void beforeChange(Change change) {
        this.type = switch (change) {
            case ObjectRemoved removed-> REMOVE;
            case NewObject ignored1 -> ADD;
            case InitialValueChange ignored -> ADD;
            default -> CHANGE;
        };
    }

    void newLine() {
        ansi.newline();
    }

    @Override
    public void afterChange(Change change) {
        append("\n");
    }

    public void onValueChange(ValueChange change) {
        switch (change) {
            case InitialValueChange valueChange ->
                    append(ADD.coloredOperation() + "\t" + change.getPropertyName() + EQUALS + quotes(change.getRight()));
            default ->
                    append(CHANGE.coloredOperation() + "\t" + change.getPropertyName() + EQUALS + quotes(change.getLeft()) + " -> " + quotes(change.getRight()));
        }
    }

    private static Object quotes(Object change) {
        if (change instanceof String string) {
            return "\"" + string + "\"";
        }
        return change;
    }

    @Override
    public void onReferenceChange(ReferenceChange change) {
        appendln(CHANGE.coloredOperation() + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onNewObject(NewObject newObject) {
        InstanceId id = (InstanceId) newObject.getAffectedGlobalId();
        append(ADD.coloredOperation() + " resource %s %s { ".formatted(id.getTypeName(), id.getCdoId()));
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        var resource = (InstanceId) objectRemoved.getAffectedGlobalId();
        appendln(REMOVE.coloredOperation() + " resource %s %s { ".formatted(resource.getTypeName(), resource.getCdoId()));
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {

    }

    @Override
    public void onSetChange(SetChange change) {
        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onArrayChange(ArrayChange change) {
        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onListChange(ListChange change) {
        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onMapChange(MapChange change) {
        appendln(type + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }


}
