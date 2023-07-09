package dev.fangscl.Diff;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fangscl.Runtime.Values.ResourceValue;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

@Log4j2
public class Printer {
    private final Map<ResourceValue, JsonNode> diffs = new HashMap<>();

    void print(ResourceValue value, JsonNode node) {
        Ansi ansi = ansi().eraseScreen();

        Change change = opWithSymbol(node.get(0));
        ansi = ansi.render("""
                \n%s resource %s %s {
                """.formatted(change.getColor().formatted(change.getSymbol()), "vm", value.getName()));
        for (JsonNode it : node) {
            Change opTextAndColor = opWithSymbol(it);


            String s = StringUtils.substringAfterLast(it.path("path").asText(), "/") + " = " + it.path("value");
            String formatted = opTextAndColor.getColor().formatted(opTextAndColor.getSymbol() + "\t" + s + "\n");
            ansi = ansi.render(formatted);
        }
        ansi = ansi.render("}");

        log.info(ansi);
    }

    @NotNull
    private static Change opWithSymbol(JsonNode jsonNode) {
        return switch (jsonNode.path("op").asText()) {
            case "replace" -> Change.CHANGE;
            case "remove" -> Change.REMOVE;
            case "add" -> Change.ADD;
            default -> throw new RuntimeException("Invalid op");
        };
    }

}
