package io.zmeu.Engine;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.Plan;
import io.zmeu.Dummy.DummyResource;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.api.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
        var dummyResource = DummyResource.builder()
                .resourceName("dummy")
                .content("dummy")
                .build();

        var mergeResult = manager.plan(dummyResource, DummyResource.class);
        var plan = new Plan();
        plan.add(mergeResult);

        manager.apply(plan);
        var src = mergeResult.resource();
        var state = (Resource) JaversUtils.mapSnapshotToObject(javers.getLatestSnapshot("dummy", DummyResource.class).get(), DummyResource.class);
        Assertions.assertEquals(src, state);
    }

}