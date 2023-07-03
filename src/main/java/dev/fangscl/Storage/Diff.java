package dev.fangscl.Storage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import dev.fangscl.Runtime.Values.ResourceValue;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static org.fusesource.jansi.Ansi.ansi;

/**
 *
 */
@Log4j2
public class Diff {
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    public Diff() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            mapper.setAccessorNaming( new DefaultAccessorNamingStrategy.Provider().withGetterPrefix( "" ).withSetterPrefix( "" ) );
    }

    @SneakyThrows
    public ResourceValue patch(ResourceValue localState, ResourceValue sourceState, ResourceValue cloudState) {
        try {
            AnsiConsole.systemInstall();

            return extracted(localState, sourceState, cloudState);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    @SneakyThrows
    private ResourceValue extracted(ResourceValue localState, ResourceValue sourceState, ResourceValue cloudState) {
        var stateJson = mapper.valueToTree(localState);
        var sourceJson = mapper.valueToTree(sourceState);
        var cloudJson = mapper.valueToTree(cloudState);
        log.warn(stateJson);
        log.warn(sourceJson);
        log.warn(cloudJson);
        log.warn("==========");

//        for (JsonNode jsonNode : sourceJson) {
//            stateJson.get(jsonNode.)
//        }

        var sourceDiff = JsonDiff.asJson(stateJson, sourceJson);
        var cloudDiff = JsonDiff.asJson(stateJson, cloudJson);
        var resDif = JsonDiff.asJson(cloudJson, sourceJson, EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE));

        log.warn("state: {}", sourceDiff);
        log.warn("cloud: {}", cloudDiff);
        log.warn("res: {}", resDif);
        log.warn("==========");

        JsonNode cloud = JsonPatch.apply(cloudDiff, stateJson);
        log.warn(cloud);
        JsonNode res = JsonPatch.apply(resDif, cloud, EnumSet.of(CompatibilityFlags.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE));
        log.warn(res);
//            log.warn(JsonPatch.apply(resDif, stateJson));


        if (sourceDiff.isEmpty() && cloudDiff.isEmpty() && stateJson.isEmpty()) {
            return sourceState;
        }
        if (resDif.isEmpty()) {
            return mapper.readValue(res.toString(), ResourceValue.class);
        }

        Ansi ansi = ansi().eraseScreen();

        ansi = ansi.render("\n%s {\n".formatted(opWithSymbol(resDif.get(0)).formatted("resource vm " + sourceState.getName())));
        for (JsonNode it : resDif) {
            var opTextAndColor = opWithSymbol(it);


            String s = StringUtils.substringAfterLast(it.path("path").asText(), "/") + " = " + it.path("value");
            ansi = ansi.render(opTextAndColor.formatted("\t" + s + "\n"));
        }
        ansi = ansi.render("}");

        log.info(ansi);

        return mapper.readValue(res.toString(), ResourceValue.class);
    }

    @NotNull
    private static String opWithSymbol(JsonNode jsonNode) {

        return switch (jsonNode.path("op").asText()) {
            case "replace" -> "@|yellow ± %s|@";
            case "remove" -> "@|red - %s|@";
            case "add" -> "@|green + %s|@";
            default -> "";
        };
    }

}
