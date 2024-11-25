package io.zmeu.CLI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ZmeuInjector {

    public static ObjectMapper createMapper() {
        var mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            mapper.setAccessorNaming( new DefaultAccessorNamingStrategy.Provider().withGetterPrefix( "" ).withSetterPrefix( "" ) );
        return mapper;
    }

    public static Zmeufile createZmeufile() throws IOException {
        var mapper = new YAMLMapper();
        var zmeufilePath = Paths.get(URI.create("file://" + Paths.get("Zmeufile.yml").toAbsolutePath()));
        var zmeufileContent = Files.readString(zmeufilePath);
        var dependencies = mapper.readValue(zmeufileContent, Dependencies.class);
        return new Zmeufile(dependencies);
    }

}
