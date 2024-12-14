package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Log4j2
class ResourceManagerTest extends JaversWithInterpreterTest {
    private ResourceManager manager;
    private PluginFactory factory;

    @BeforeEach
    void setUp() {
        YAMLMapper yamlMapper = YAMLMapper.builder().build();
        System.setProperty("pf4j.mode", "development");
        System.setProperty("pf4j.logLevel", "DEBUG");
        System.setProperty("pf4j.pluginsDir", "src/test/java/io/zmeu/Plugin");
        factory = new PluginFactory(new Zmeufile(new Dependencies(List.of())));
        manager = new ResourceManager(factory.getPluginHashMap(), yamlMapper, new Diff(javers, mapper));
        factory.loadPlugins();
    }

    @AfterEach
    void tearDown() {
        factory.stopPlugins();
    }

    @Test
    void resourceDependencyIsAdded() {
        var schemas = factory.schemas() + """
                resource TypeResource main {
                
                }
                """;

        var evalRes = interpreter.eval(checker.eval(parser.produceAST(tokenizer.tokenize(schemas))));
        var resources = interpreter.resourcesGroupedBySchema();

        manager.plan(resources);
    }
//
//    @Test
//    void resourceDependencyReverseOrder() {
//        var res = eval("""
//                schema vm {
//                    var name:String
//                    var maxCount=0
//                }
//
//                resource vm second {
//                    name = "second"
//                    maxCount = vm.main.maxCount
//                }
//
//                resource vm main {
//                    name = "first"
//                    maxCount=1
//                }
//                """);
//        var vm = (SchemaValue) global.lookup("vm");
//        var second = vm.getInstance("second");
//        assertEquals(2, engine.getResources().size());
//        assertEquals(second, engine.getResources().get(0));
//    }

}