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
import dev.fangscl.Backend.Resource;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 *
 */
@Log4j2
public class Diff {
    public static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(
            DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE,
            DiffFlags.OMIT_COPY_OPERATION,
            DiffFlags.OMIT_MOVE_OPERATION
    );
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
    public JsonNode apply(Resource localState, Resource sourceState, @Nullable Resource cloudState) {
        try {
            AnsiConsole.systemInstall();
            var stateJson = mapper.valueToTree(localState);
            var sourceJson = mapper.valueToTree(sourceState);
            var cloudJson = mapper.valueToTree(cloudState);
            log.warn("\nstate {}\nsrc {}\ncloud {}", stateJson, sourceJson, cloudJson);
            //        log.warn("==========");
            return threeWayMerge(stateJson, sourceJson, cloudJson);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    @SneakyThrows
    private JsonNode threeWayMerge(JsonNode stateJson, JsonNode sourceJson, JsonNode cloudJson) {
        // set common base
//        sourceJson = mapper.readerForUpdating(mapper.valueToTree(stateJson)).readValue(sourceJson);
//        cloudJson = mapper.readerForUpdating(mapper.valueToTree(stateJson)).readValue(cloudJson);
//        sourceJson = mapper.readerForUpdating(sourceJson.findValue("properties")).readValue(stateJson.findValue("hidden"));
        var sourceLocalDiff = JsonDiff.asJson(stateJson, sourceJson, DIFF_FLAGS);
        var remoteLocalDiff = JsonDiff.asJson(stateJson, cloudJson, DIFF_FLAGS);

        var cloud = JsonPatch.apply(remoteLocalDiff, stateJson);
        var src = JsonPatch.apply(sourceLocalDiff, stateJson);
        var srcRemoteDiff = JsonDiff.asJson(cloud, src, DIFF_FLAGS);

//        log.warn("state: {}", sourceLocalDiff);
//        log.warn("cloud: {}", remoteLocalDiff);
        log.warn("res: {}", srcRemoteDiff);
//        log.warn("==========");

        JsonPatch.applyInPlace(srcRemoteDiff, cloudJson);
//            log.warn(JsonPatch.apply(srcRemoteDiff, stateJson));


        if (cloudJson.isEmpty()) {
            return sourceJson;
        }
        if (srcRemoteDiff.isEmpty()) {
            return cloudJson;
        }

        printer.print(sourceJson, srcRemoteDiff);

        return cloudJson;
    }

    public JsonNode toJsonNode(Object object) {
        return mapper.valueToTree(object);
    }
}
