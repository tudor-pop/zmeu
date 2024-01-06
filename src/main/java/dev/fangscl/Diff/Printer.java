package dev.fangscl.Diff;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class Printer {
    private final Map<JsonNode, JsonNode> diffs = new HashMap<>();

    public void print(Plan plan) {
        print(plan.sourceCode(), plan.diffResults());
    }

    void print(@Nullable JsonNode value, @Nullable JsonNode node) {
        if (node == null || value == null) {
            return;
        }
        Ansi ansi = ansi().eraseScreen();

        Change change = opWithSymbol(node);
        ansi = ansi.newline()
                .render("""
                        %s resource %s %s {
                        """.formatted(change.coloredOperation(), value.get("type").asText(), value.get("name").asText()));

        for (JsonNode it : node) {
            Change opTextAndColor = opWithSymbol(it);

            String str = switch (opTextAndColor) {
                case ADD -> it.findValue("properties")
                        .properties()
                        .stream()
                        .map(Printer::formatNode)
                        .collect(Collectors.joining());
                default -> "%s = %s".formatted(StringUtils.substringAfterLast(it.path("path").asText(), "/"),
                        it.path("value"));
            };
            String formatted = opTextAndColor.color(opTextAndColor.getSymbol() + "\t" + str);
            ansi = ansi.render(formatted).newline();
        }
        ansi = ansi.render(change.coloredOperation() + " }");

        log.info(ansi);
    }

    @NotNull
    private static String formatNode(Map.Entry<String, JsonNode> stringJsonNodeEntry) {
        return stringJsonNodeEntry.getKey() + " = " + stringJsonNodeEntry.getValue();
    }

    @NotNull
    private static Change opWithSymbol(JsonNode jsonNode) {
        return switch (jsonNode.findValue("op").asText()) {
            case "replace" -> Change.COLORED_OPERATION;
            case "remove" -> Change.REMOVE;
            case "add" -> Change.ADD;
            default -> throw new RuntimeException("Invalid op");
        };
    }

}
