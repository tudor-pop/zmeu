package io.zmeu.javers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Diff.ResourceChange;
import io.zmeu.Resource.Identity;
import io.zmeu.Resource.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.fusesource.jansi.Ansi;
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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.zmeu.Diff.ResourceChange.*;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ResourceChangeLog implements ChangeProcessor<String> {
    @Setter
    private ResourceChange type = NO_OP;
    private Ansi ansi;
    private boolean enableStdout;
    private static final String EQUALS = " = ";
    private boolean resourcePrinted = false;
    @Getter
    private Resource resource;
    private final ObjectMapper mapper;
    private final StringBuilder builder = new StringBuilder();

    public ResourceChangeLog(boolean enableStdout, ObjectMapper mapper) {
        this.enableStdout = enableStdout;
        this.mapper = mapper;
    }

    @Override
    public void beforeChangeList() {
        ansi = ansi().eraseScreen();
        newLine();
        builder.setLength(0);
        resourcePrinted = false;
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
        ansi.eraseScreen();
    }

    @Override
    public void beforeChange(Change change) {
        if (change.getAffectedObject().isEmpty()) {
            return;
        }

        Object o = change.getAffectedObject().get();
        if (o instanceof Resource res) {
            this.resource = res;
        } else if (o instanceof Identity res) {
            this.resource = res.getResource();
        }
        this.type = switch (change) {
            case ObjectRemoved removed -> this.resource.isReplace() ? REPLACE : REMOVE;
            case NewObject ignored1 -> this.resource.isReplace() ? REPLACE : ADD;
            case InitialValueChange ignored -> this.resource.isReplace() ? REPLACE : ADD;
            default -> this.resource.isReplace() ? REPLACE : CHANGE;
        };

        if (!resourcePrinted) {
            resourcePrinted = true;
            append(formatResource(type, this.resource));
        }

    }

    void newLine() {
        ansi.newline();
    }

    @Override
    public void afterChange(Change change) {
//        append("\n");
        if (this.resource.isReplace()) {
            this.type = REPLACE;
            return;
        }
        this.type = switch (change) {
            case ObjectRemoved removed -> REMOVE;
            case TerminalValueChange removed -> REMOVE;
            case NewObject object -> ADD;
            case InitialValueChange ignored -> ADD;
            case PropertyChange ignored -> CHANGE;
            default -> NO_OP;
        };
    }

    @Override
    public void onPropertyChange(PropertyChange propertyChange) {

    }

    public void onValueChange(ValueChange change) {
        var left = change.getLeft();

        var resource = Optional.ofNullable(change.getRight()).orElse(left);
        if (!change.getPropertyName().equals("properties")) return; // only print resource properties, not anything else

        var attributes = mapper.convertValue(resource, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        int maxPropLen = attributes.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
        append("\n");
        switch (change) {
            case InitialValueChange valueChange -> formatProperty(attributes, ADD, maxPropLen);
            case TerminalValueChange valueChange -> formatProperty(attributes, REMOVE, maxPropLen);
            case ValueChange valueChange -> valueChange(left, attributes, maxPropLen);
        }
    }

    private void valueChange(Object left, LinkedHashMap<String, Object> attributes, int maxPropLen) {
        var attributesLeft = mapper.convertValue(left, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        /*
         * compute where how many spaces after property value -> should be placed. Only consider properties that have changed
         * -	name    = "local"  -> null
         * ~	content = "remote" -> "src"
         * */
        int maxValueLen = attributesLeft.entrySet().stream()
                                  .filter(it -> !Objects.equals(attributes.get(it.getKey()), it.getValue()))
                                  .map(it -> {
                                      if (it.getValue() instanceof String string) {
                                          return string.length();
                                      }
                                      return 1;
                                  })
                                  .max(Integer::compareTo)
                                  .orElse(1) + 3; // 3 for quotes "" and one extra space. Alternative is more computing in the map like: quotes(s).length();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String property = entry.getKey();
            Object value = entry.getValue();
            Object change1 = attributesLeft.get(property);
            if (Objects.equals(change1, value)) {
                appendln(("\t%-" + maxPropLen + "s%s%s").formatted(property, EQUALS, quotes(value)));
            } else if (change1 != null && value == null) {
                appendln(("%s\t%-" + maxPropLen + "s%s%-" + maxValueLen + "s%s %s").formatted(REMOVE.toColor(), property, EQUALS, quotes(change1), ARROW.toColor(), ARROW.color("null")));
            } else {
                var color = this.resource.hasImmutablePropetyChanged(property) ? REPLACE : CHANGE;
                appendln(("%s\t%-" + maxPropLen + "s%s%-" + maxValueLen + "s%s %s").formatted(color.toColor(), property, EQUALS, quotes(change1), color.color(ARROW.getSymbol()), quotes(value)));
            }
        }
    }

    private void formatProperty(LinkedHashMap<String, Object> attributes, ResourceChange color, int maxPropLen) {
        attributes.forEach((property, value) ->
                // %-10s Left justifies the output. Spaces ('\u0020') will be added at the end of the converted value as required to fill the minimum width of the field
                appendln(("%s\t%-" + maxPropLen + "s%s%s").formatted(color.toColor(), property, EQUALS, quotes(value)))
        );
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

    }

    private @NotNull String formatResource(ResourceChange coloredChange, Resource resource) {
        var text = "";
        if (resource.isReplace()) {
            text = REPLACE.color(" # marked for replace");
        }
        if (resource.resourceName().getRenamedFrom() != null) {
            return coloredChange.toColor() + " resource %s %s %s %s {%s".formatted(resource.getKind(), resource.resourceName().getRenamedFrom(), coloredChange.color(ARROW.getSymbol()), resource.resourceName().getName(), text);
        }
        return coloredChange.toColor() + " resource %s %s {%s".formatted(resource.getKind(), resource.getIdentity().getName(), text);
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        if (this.resource.isReplace()) {
            return; // we can choose to handle replace in onNewObject or onObjectRemoved. We handle it in onNewObject
        }
        newLine();
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

    @Override
    public String result() {
        return builder.toString();
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {

    }

    @Override
    public void onAffectedObject(GlobalId globalId) {

    }

    protected void append(String text) {
        if (text != null) {
            builder.append(text);
        }
    }

    /**
     * null safe
     */
    protected void append(Object text) {
        if (text != null) {
            builder.append(text.toString());
        }
    }

    /**
     * null safe
     */
    protected void appendln(String text) {
        if (text != null) {
            builder.append(text + "\n");
        }
    }

    /**
     * null safe
     */
    protected void appendln(Object text) {
        if (text != null) {
            builder.append(text.toString() + "\n");
        }
    }
}
