package io.zmeu.javers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.ResourceChange;
import io.zmeu.api.resource.Resource;
import lombok.Setter;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.zmeu.Diff.ResourceChange.*;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ResourceChangeLog extends AbstractTextChangeLog {
    @Setter
    private ResourceChange type = NO_OP;
    private Ansi ansi;
    private boolean enableStdout;
    private InstanceId id;
    private static final String EQUALS = "\t= ";
    private boolean resourcePrinted = false;
    private Resource resource;
    private ObjectMapper mapper = new ObjectMapper();

    public ResourceChangeLog(boolean enableStdout) {
        this();
        this.enableStdout = enableStdout;
    }

    public ResourceChangeLog() {
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
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
        this.id = (InstanceId) globalId;
    }

    @Override
    public void beforeChange(Change change) {
        this.type = switch (change) {
            case ObjectRemoved removed -> REMOVE;
            case NewObject ignored1 -> ADD;
            case InitialValueChange ignored -> ADD;
            default -> CHANGE;
        };

        if (this.id == null && change.getAffectedGlobalId() instanceof InstanceId instanceId) {
            this.id = instanceId;
        }
        if (change.getAffectedObject().isPresent() && change.getAffectedObject().get() instanceof Resource resource) {
            this.resource = resource;
        }
        if (!resourcePrinted) {
            resourcePrinted = true;
            append(getText(type, this.resource));
        }
    }

    void newLine() {
        ansi.newline();
    }

    @Override
    public void afterChange(Change change) {
//        append("\n");
        this.type = switch (change) {
            case ObjectRemoved removed -> REMOVE;
            case TerminalValueChange removed -> REMOVE;
            case NewObject object -> ADD;
            case InitialValueChange ignored -> ADD;
            case PropertyChange ignored -> CHANGE;
            default -> NO_OP;
        };
    }

    public void onValueChange(ValueChange change) {
        var right = change.getRight();
        if (right instanceof String) return;

        var attributes = mapper.convertValue(right, new TypeReference<LinkedHashMap<String, Object>>() {});
        switch (change) {
            case InitialValueChange valueChange ->
                    attributes.forEach((property, value) ->
                            appendln("%s\t%s%s%s".formatted(ADD.coloredOperation(), property, EQUALS, quotes(value)))
                    );
            case TerminalValueChange valueChange ->
                    attributes.forEach((property,value)->
                            appendln("%s\t%s%s%s".formatted(REMOVE.coloredOperation(), property, EQUALS, quotes(value)))
                    );
            default ->
                    attributes.forEach((property,value)->
                            appendln("\n%s\t%s%s%s -> %s".formatted(CHANGE.coloredOperation(), property, EQUALS, quotes(change.getLeft()), quotes(value)))
                    );
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
        append("\n");
    }

    private @NotNull String getText(ResourceChange coloredChange,  Resource resource) {
        return coloredChange.coloredOperation() + " resource %s %s {".formatted(resource.getResourceName(), resource.getType());
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
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
