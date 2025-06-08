package io.zmeu.javers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static io.zmeu.Diff.ChangeColor.ARROW;
import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class ResourceChangeLog implements ChangeProcessor<String> {
    public static final String FG_PROPERTY_COLOR = "\u001B[38;2;0;122;204m";
    public static final String FG_RESOURCE_NAME_COLOR = "\u001B[38;2;175;95;175m";
    public static final String FG_GREY_COLOR = "\u001B[38;2;95;95;95m";
    public static final String FG_STRING_COLOR = "\u001B[38;2;46;160;100m";
    private final Map<Ansi.Color, String> colors = Map.of(
            Ansi.Color.RED, "-",
            Ansi.Color.GREEN, "+",
            Ansi.Color.YELLOW, "~",
            Ansi.Color.MAGENTA, "±",
            Ansi.Color.CYAN, "<="
    );
    @Setter
    private Ansi.Color color = Ansi.Color.DEFAULT;

    private Ansi ansi;
    private static final String EQUALS = " = ";
    private boolean resourcePrinted = false;
    @Getter
    private Resource resource;
    private final ObjectMapper mapper;
//    private final StringBuilder builder = new StringBuilder();

    public ResourceChangeLog(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void beforeChangeList() {
        ansi = ansi(50)
                .reset()
                .eraseScreen();
        newLine();
        resourcePrinted = false;
    }

    @Override
    public void afterChangeList() {
        if (color != Ansi.Color.DEFAULT) {
            ansi.fg(color)
                    .a(colors.get(color))
                    .reset()
                    .a(" }")
                    .newline();
        }
        log.info(ansi.toString());
        ansi.reset().eraseScreen();
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
        updateType(change);

        if (!resourcePrinted) {
            resourcePrinted = true;
            append(formatResource(color, this.resource));
        }

    }

    void newLine() {
        ansi.newline();
    }

    @Override
    public void afterChange(Change change) {
        updateType(change);
    }

    private void updateType(Change change) {
        if (this.resource.isReplace()) {
            this.color = Ansi.Color.MAGENTA;
        } else if (resource.isExisting()) {
            this.color = Ansi.Color.CYAN;
        } else {
            this.color = switch (change) {
                case ObjectRemoved removed -> Ansi.Color.RED;
                case TerminalValueChange removed -> Ansi.Color.RED;
                case NewObject object -> Ansi.Color.GREEN;
                case InitialValueChange ignored -> Ansi.Color.GREEN;
                case PropertyChange ignored -> Ansi.Color.YELLOW;
                default -> Ansi.Color.DEFAULT;
            };
        }
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
//        append("\n");
        if (this.resource.isExisting()) {
            formatProperty(attributes, Ansi.Color.MAGENTA, maxPropLen);
        } else {
            switch (change) {
                case InitialValueChange valueChange -> formatProperty(attributes, Ansi.Color.GREEN, maxPropLen);
                case TerminalValueChange valueChange -> formatProperty(attributes, Ansi.Color.RED, maxPropLen);
                case ValueChange valueChange -> valueChange(left, attributes, maxPropLen);
            }
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
                ansi.a("\t")
                        .format("%-" + maxPropLen + "s", property)
                        .a(EQUALS)
                        .a(FG_STRING_COLOR)
                        .a(quotes(change1))
                        .reset()
                        .newline();
            } else if (change1 != null && value == null) {
                ansi.fg(Ansi.Color.RED)
                        .a(colors.get(Ansi.Color.RED))
                        .a('\t')
                        .format("%-" + maxPropLen + "s", property)
                        .a(EQUALS)
//                        .fg(Ansi.Color.RED)
                        .format("%-" + maxValueLen + "s", quotes(change1))
                        .a(FG_GREY_COLOR)
                        .a("-> null")
                        .newline();
            } else {
                var color = this.resource.hasImmutablePropetyChanged(property) ? Ansi.Color.MAGENTA : Ansi.Color.YELLOW;
                ansi.fg(color)
                        .a(colors.get(color))
                        .a("\t")
                        .format("%-" + maxPropLen + "s", property)
                        .a(" = ")
//                        .a(FG_STRING_COLOR)
                        .a(quotes(change1))
                        .fg(color)
                        .a(" -> ")
                        .a(quotes(value))
                        .a("\n");
            }
        }
    }

    private void formatProperty(LinkedHashMap<String, Object> attributes, Ansi.Color color, int maxPropLen) {
        attributes.forEach((property, value) -> {
            // %-10s Left justifies the output. Spaces ('\u0020') will be added at the end of the converted value as required to fill the minimum width of the field
            ansi.fg(this.color)
                    .a(colors.get(this.color))
                    .reset();
            ansi.a("\t")
                    .format("%-" + maxPropLen + "s%s", property, EQUALS)
                    .a(quotes(value))
                    .reset()
                    .newline();

        });
    }

    private Object quotes(Object change) {
        if (change == null) {
            ansi.a(FG_GREY_COLOR);
            return "null";
        }
        if (change instanceof String string) {
            return "\"" + string + "\"";
        }
        return change;
    }

    @Override
    public void onReferenceChange(ReferenceChange change) {
        ansi.fg(Ansi.Color.YELLOW)
                .a("\t")
                .a(change.getPropertyName())
                .reset()
                .a("=")
                .a(change.getLeft())
                .a(FG_GREY_COLOR)
                .a(change.getRight())
                .reset();
    }

    @Override
    public void onNewObject(NewObject newObject) {

    }

    private @NotNull String formatResource(Ansi.Color coloredChange, Resource resource) {
        Ansi line = ansi();

        // Left symbol
        line.fg(coloredChange)
                .a(colors.get(coloredChange))
                .reset()
                .a(FG_PROPERTY_COLOR)
                .a(" resource ")
                .a(Ansi.Attribute.RESET)
                .a(resource.getKind())
                .reset()
                .a(" ");

        if (resource.resourceName().getRenamedFrom() != null) {
            line.a(resource.resourceName().getRenamedFrom())
                    .a(" ")
                    .fg(Ansi.Color.MAGENTA)
                    .a(ARROW.getSymbol())
                    .reset()
                    .a(" ")
                    .a(resource.resourceName().getName())
                    .a(" ");
        } else {
            line.a(resource.getIdentity().getName())
                    .reset()
                    .a(" ");
        }

        line.a("{");

        // Suffix if replaced
        if (resource.isReplace()) {
            line.fg(Ansi.Color.MAGENTA)
                    .a(" # marked for replace")
                    .reset();
        }


        return line.toString();
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        if (this.resource.isReplace()) {
            return; // we can choose to handle replace in onNewObject or onObjectRemoved. We handle it in onNewObject
        }
//        newLine();
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {

    }

    @Override
    public void onSetChange(SetChange change) {
        appendln(color + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());
    }

    @Override
    public void onArrayChange(ArrayChange change) {
        appendln(color + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onListChange(ListChange change) {
        appendln(color + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public void onMapChange(MapChange change) {
        appendln(color + "\t" + change.getPropertyName() + EQUALS + change.getLeft() + " -> " + change.getRight());

    }

    @Override
    public String result() {
        return ansi.toString();
    }

    public static String stripAnsi(String input) {
        // Matches any ANSI escape code: ESC [ ... letters like m, J, K, etc.
        return input.replaceAll("\\u001B\\[[;\\d]*[ -/]*[@-~]", "").trim();
    }

    public static String tokenizeAnsi(String input) {
        return input
                .replaceAll("\u001B\\[2J", "") // Clear screen
                // Standard ANSI colors
                .replaceAll("\u001B\\[31m", "[RED]")
                .replaceAll("\u001B\\[32m", "[GREEN]")
                .replaceAll("\u001B\\[33m", "[YELLOW]")
                .replaceAll("\u001B\\[34m", "[BLUE]")
                .replaceAll("\u001B\\[35m", "[MAGENTA]")
                .replaceAll("\u001B\\[36m", "[CYAN]")
                // RGB colors
                .replaceAll("\u001B\\[38;2;0;122;204m", "[BLUE]") // RGB blue
                .replaceAll("\u001B\\[38;2;46;160;100m", "[GREEN-STR]")
                .replaceAll("\u001B\\[38;2;95;95;95m", "[DARK-GREY]")
                // Resets
                .replaceAll("\u001B\\[0m", "") // Explicit reset
                .replaceAll("\u001B\\[m", "")
                .trim(); // Implicit reset (this is your missing piece!)

    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {

    }

    @Override
    public void onAffectedObject(GlobalId globalId) {

    }

    protected void append(String text) {
        if (text != null) {
            ansi.append(text).append("\n");
        }
    }

    /**
     * null safe
     */
    protected void append(Object text) {
        if (text != null) {
            ansi.append(text.toString()).append("\n");
        }
    }

    /**
     * null safe
     */
    protected void appendln(String text) {
        if (text != null) {
            ansi.append(text).append("\n");
        }
    }

    /**
     * null safe
     */
    protected void appendln(Object text) {
        if (text != null) {
            ansi.append(text.toString()).append("\n");
        }
    }
}
