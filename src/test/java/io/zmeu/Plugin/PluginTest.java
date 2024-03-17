package io.zmeu.Plugin;

import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.Runtime.BaseTest;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.api.Provider;
import lombok.extern.log4j.Log4j2;
import org.javers.common.reflection.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
public class PluginTest extends BaseTest {
    private CustomPluginManager pluginManager;

    @BeforeEach
    void init() {
        pluginManager = PluginFactory.create();
    }

    @Test
    void resourceIsDefinedInSchema() {
        var extensions = pluginManager.getExtensions(Provider.class, "files-0.0.1");
        var providerSchema = extensions.getFirst().schemasString();
        var res = eval(providerSchema + """
                resource File main {
                    name = "first"
                    content = "provider schema"
                }
                resource File second {
                    name = "second"
                    content = File.main.content
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("File");

        assertNotNull(schema);
        assertEquals("File", schema.getType().getSymbol());

        var resource = (ResourceValue) schema.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
        assertEquals("first", resource.argVal("name"));
        assertEquals("provider schema", resource.argVal("content"));

        var second = (ResourceValue) schema.getInstances().get("second");

        assertNotNull(second);
        assertEquals("second", second.getName());
        assertEquals("second", second.argVal("name"));
        assertEquals("provider schema", second.argVal("content"));

        var pname = extensions.getFirst().items().getResources().getFirst().getClass().getPackageName();
        var file = ReflectionUtil.classForName(pname + ".FileResource");
        log.info(file);
    }
}
