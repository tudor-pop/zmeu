package io.zmeu.javers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.ResourceChange;
import io.zmeu.api.resource.Resource;
import lombok.Getter;
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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

import static io.zmeu.Diff.ResourceChange.*;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ResourceChangeLog extends AbstractTextChangeLog {
    @Setter
    private ResourceChange type = NO_OP;
    private Ansi ansi;
    private boolean enableStdout;
    private static final String EQUALS = "\t= ";
    private boolean resourcePrinted = false;
    @Getter
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
            append(type.toColor() + " }");
        }
        ansi = ansi.render(result());
        if (enableStdout) {
            log.info(ansi);
        }
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
    }

    @Override
    public void beforeChange(Change change) {
        this.type = switch (change) {
            case ObjectRemoved removed -> REMOVE;
            case NewObject ignored1 -> ADD;
            case InitialValueChange ignored -> ADD;
            default -> CHANGE;
        };


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
        if (!change.getPropertyName().equals("resource")) return; // only print resource properties, not anything else

        var attributes = mapper.convertValue(right, new TypeReference<LinkedHashMap<String, Object>>() {});
        switch (change) {
            case InitialValueChange valueChange ->
                    attributes.forEach((property, value) ->
                            appendln("%s\t%s%s%s".formatted(ADD.toColor(), property, EQUALS, quotes(value)))
                    );
            case TerminalValueChange valueChange ->
                    attributes.forEach((property,value)->
                            appendln("%s\t%s%s%s".formatted(REMOVE.toColor(), property, EQUALS, quotes(value)))
                    );
            default ->
                    attributes.forEach((property,value)->
                            appendln("\n%s\t%s%s%s -> %s".formatted(CHANGE.toColor(), property, EQUALS, quotes(change.getLeft()), quotes(value)))
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
        appendln(CHANGE.toColor() + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onNewObject(NewObject newObject) {
        append("\n");
    }

    private @NotNull String getText(ResourceChange coloredChange,  Resource resource) {
        return coloredChange.toColor() + " resource %s %s {".formatted(resource.getResourceName(), resource.getType());
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
