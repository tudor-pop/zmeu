package dev.fangscl.Diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import dev.fangscl.Runtime.Values.ResourceValue;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

/**
 *
 */
@Log4j2
public class Diff {
    public static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE, DiffFlags.OMIT_COPY_OPERATION, DiffFlags.OMIT_MOVE_OPERATION);
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private final Printer printer = new Printer();

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
        log.warn("\n{}\n{}\n{}", stateJson, sourceJson, cloudJson);
//        log.warn("==========");

        // set common base
        sourceJson = mapper.readerForUpdating(mapper.valueToTree(localState)).readValue(sourceJson);
        cloudJson = mapper.readerForUpdating(mapper.valueToTree(localState)).readValue(cloudJson);

//        var sourceLocalDiff = JsonDiff.asJson(stateJson, sourceJson, DIFF_FLAGS);
//        var remoteLocalDiff = JsonDiff.asJson(stateJson, cloudJson, DIFF_FLAGS);
        var srcRemoteDiff = JsonDiff.asJson(cloudJson, sourceJson, DIFF_FLAGS);

//        log.warn("state: {}", sourceLocalDiff);
//        log.warn("cloud: {}", remoteLocalDiff);
        log.warn("res: {}", srcRemoteDiff);
//        log.warn("==========");

//        JsonNode cloud = JsonPatch.apply(remoteLocalDiff, stateJson);
        JsonPatch.applyInPlace(srcRemoteDiff, cloudJson);
//            log.warn(JsonPatch.apply(srcRemoteDiff, stateJson));


        if (cloudJson.isEmpty()) {
            return sourceState;
        }
        if (srcRemoteDiff.isEmpty()) {
            return mapper.readValue(cloudJson.toString(), ResourceValue.class);
        }

        printer.print(sourceState, srcRemoteDiff);

        return mapper.readValue(cloudJson.toString(), ResourceValue.class);
    }
}
