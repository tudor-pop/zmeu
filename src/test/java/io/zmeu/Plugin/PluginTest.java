package io.zmeu.Plugin;

import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Dependency;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Runtime.BaseRuntimeTest;
import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.api.Provider;
import lombok.extern.log4j.Log4j2;
import org.javers.common.reflection.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pf4j.DefaultPluginManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
public class PluginTest extends BaseRuntimeTest {
    private DefaultPluginManager pluginManager;
    private Zmeufile zmeufile;

    @BeforeEach
    void init() {
        zmeufile = new Zmeufile(new Dependencies(List.of()));
        pluginManager = new PluginFactory(zmeufile).loadPlugins();
    }

    @Test
    void resourceIsDefinedInSchema() {
        for (Dependency dependency : zmeufile.dependencies().dependencies()) {
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
            assertEquals("File", schema.getType());

            ResourceValue resource = schema.getInstance("main");

            assertNotNull(resource);
            assertEquals("main", resource.getName());
            assertEquals("first", resource.argVal("name"));
            assertEquals("provider schema", resource.argVal("content"));

            ResourceValue second = schema.getInstance("second");

            assertNotNull(second);
            assertEquals("second", second.getName());
            assertEquals("second", second.argVal("name"));
            assertEquals("provider schema", second.argVal("content"));

            var pname = extensions.getFirst().resources().list().getFirst().getClass().getPackageName();
            var file = ReflectionUtil.classForName(pname + ".FileResource");
            log.info(file);

        }
    }

//    @Test
//    void resourceDiffApply() {
//        for (Dependency dependency : zmeufile.dependencies().dependencies()) {
//            var extensions = pluginManager.getExtensions(Provider.class, dependency.uri());
//            Provider first = extensions.getFirst();
//            var providerSchema = first.schemasString();
//            var res = eval(providerSchema + """
//                    resource File main {
//                        name    = "fisier.txt"
//                        content = "provider schema"
//                    }
//                    """);
//            var schema = (SchemaValue) global.get("File");
//            var pname = extensions.getFirst().resources().dependencies().getFirst().getClass().getName();
//            var file = ReflectionUtil.classForName(pname);
//
//            log.warn(toJson(res));
//        }
//    }
}
