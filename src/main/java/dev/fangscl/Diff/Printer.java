package dev.fangscl.Diff;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class Printer {
    private final Map<JsonNode, JsonNode> diffs = new HashMap<>();

    void print(JsonNode value, JsonNode node) {
        Ansi ansi = ansi().eraseScreen();

        Change change = opWithSymbol(node);
        ansi = ansi.newline()
                .render("""
                %s resource %s %s {
                """.formatted(change.coloredOperation(), value.get("type").asText(), value.get("name").asText()));

        for (JsonNode it : node) {
            Change opTextAndColor = opWithSymbol(it);

            String s = "%s = %s".formatted(StringUtils.substringAfterLast(it.path("path").asText(), "/"), it.path("value"));
            String formatted = opTextAndColor.color(opTextAndColor.getSymbol() + "\t" + s);
            ansi = ansi.render(formatted).newline();
        }
        ansi = ansi.render("}");

        log.info(ansi);
    }

    @NotNull
    private static Change opWithSymbol(JsonNode jsonNode) {
        return switch (jsonNode.findValue("op").asText()) {
            case "replace" -> Change.CHANGE;
            case "remove" -> Change.REMOVE;
            case "add" -> Change.ADD;
            default -> throw new RuntimeException("Invalid op");
        };
    }

}
