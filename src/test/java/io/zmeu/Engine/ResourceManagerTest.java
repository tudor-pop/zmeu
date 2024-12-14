package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Base.JaversTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.Runtime.Values.SchemaValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Log4j2
class ResourceManagerTest extends JaversTest {
    private ResourceManager manager;

    @BeforeEach
    void setUp() {
        YAMLMapper yamlMapper = YAMLMapper.builder().build();
        PluginFactory factory = new PluginFactory(new Zmeufile(new Dependencies(List.of())));
        manager = new ResourceManager(factory, yamlMapper, new Diff(javers, mapper));
    }

    @Test
    void resourceDependencyIsAdded() {

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