package io.zmeu.Plugin;

import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Dependency;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.config.CustomPluginManager;
import io.zmeu.Runtime.BaseTest;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.api.Provider;
import lombok.extern.log4j.Log4j2;
import org.javers.common.reflection.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
public class PluginTest extends BaseTest {
    private CustomPluginManager pluginManager;
    private Zmeufile zmeufile;

    @BeforeEach
    void init() {
        zmeufile = new Zmeufile(new Dependencies(List.of(new Dependency("files@0.0.1"))));
        pluginManager = PluginFactory.create(zmeufile);
    }

    @Test
    void resourceIsDefinedInSchema() {
        for (Dependency dependency : zmeufile.dependencies().list()) {
            var extensions = pluginManager.getExtensions(Provider.class, dependency.uri());
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
}
