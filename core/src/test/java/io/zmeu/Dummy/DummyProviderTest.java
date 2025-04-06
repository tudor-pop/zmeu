package io.zmeu.Dummy;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Diff.Plan;
import io.zmeu.Engine.JaversUtils;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.PluginFactory;
import io.zmeu.api.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

@Log4j2
class DummyProviderTest extends JaversWithInterpreterTest {
    private ResourceManager manager;
    private PluginFactory factory;

    @BeforeEach
    void setUp() {
        var yamlMapper = YAMLMapper.builder().build();
        var diff = new Diff(JaversFactory.createNoDb(), mapper);
        factory = new PluginFactory(new Zmeufile(new Dependencies(List.of())));
        manager = new ResourceManager(factory.getPluginHashMap(), yamlMapper, diff);
        factory.loadPlugins();
    }

    @AfterEach
    void tearDown() {
        factory.stopPlugins();
    }

    /**
     * resource doesn't exist in cloud or state
     */
    @Test
    void resourceMissingCloudAndState() {
        eval("""
                schema DummyResource {
                    var content String
                }
                resource dummy DummyResource {
                   content = "some content";
                }
                """);
        interpreter.eval(program);


        var resources = interpreter.getResources();
        var plan = manager.plan(resources);
        manager.apply(plan);

        var src = plan.getMergeResults().getFirst().resource();
        Assertions.assertNotNull(src); // assert it gets evaluated

        var resourceFromState = javers.getLatestSnapshot("dummy", Resource.class).get();
        var state = JaversUtils.mapSnapshotToObject(resourceFromState, Resource.class);
        mapper.map(resourceFromState, DummyResource.class);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);

        var provider = manager.getProvider(DummyResource.class);
        var cloud = provider.read(src);
        Assertions.assertEquals(src, cloud);
    }

    /**
     * resource exist in src and cloud but not in javers not should show cli output
     * when cloud resource exists in src and cloud but not in javers, just update javers data
     * no changes should be shown in cli output but the objects should be commited to jvers
     */
    @Test
    void addResourceToJaversExistingResourceInCloud() {
        var resource = new Resource("dummy",
                DummyResource.builder()
                .content("dummy")
                .build()
        );


        var provider = manager.getProvider(DummyResource.class);
        provider.create(resource);

        var mergeResult = manager.plan(resource);
        var plan = new Plan();
        plan.add(mergeResult);

        // when cloud resource exists in src and cloud but not in javers, just update javers data
        // no changes should be shown in cli output but the objects should be commited to jvers
        Assertions.assertTrue(mergeResult.changes().isEmpty());
        Assertions.assertNotNull(mergeResult.resource());

        manager.apply(plan);
        var src = mergeResult.resource();
        var dummy = javers.getLatestSnapshot("dummy", Resource.class).get();
        var state = JaversUtils.mapSnapshotToObject(dummy, Resource.class);
        state.setResource(mapper.map(state.getResource(), factory.getSchema(state.getType())));
        Assertions.assertEquals(src, state);

        var cloud = provider.read(src);
        Assertions.assertEquals(src, cloud);
        Assertions.assertEquals(src, state);
    }

    /**
     * resource exist in src and state but not in cloud should show cli output as ADD operation
     */
//    @Test
//    void resourceExistsSrcAndJaversMissingCloud() {
//        var resource = Resource.builder()
//                .resourceName("dummy")
//                .resource(DummyResource.builder()
//                        .content("dummy")
//                        .build())
//                .build();
//
//        var provider = manager.getProvider(DummyResource.class);
//        javers.commit("dummy", resource);
//
//        var mergeResult = manager.plan(resource);
//        var plan = new Plan();
//        plan.add(mergeResult);
//
//        Assertions.assertFalse(mergeResult.changes().isEmpty());
//        Assertions.assertNotNull(mergeResult.resource());
//
//        manager.apply(plan);
//        var src = mergeResult.resource();
//        var dummy = javers.getLatestSnapshot("dummy", Resource.class).get();
//        var state = (Resource) JaversUtils.mapSnapshotToObject(dummy, Resource.class);
//        Assertions.assertEquals(src, state);
//
//        var cloud = provider.read(src);
//        Assertions.assertEquals(src, cloud);
//        Assertions.assertEquals(src, state);
//    }

    /**
     * Update resource using provider
     */
//    @Test
//    void resourceUpdated() {
//        var provider = manager.getProvider(DummyResource.class);
//
//        var dummyResource = DummyResource.builder()
//                .resourceName("dummy")
//                .content("dummy")
//                .build();
//        var initMerge = manager.plan(dummyResource, DummyResource.class);
//        var initPlan = new Plan();
//        initPlan.add(initMerge);
//        manager.apply(initPlan);
//
//        var dummyUpdated = DummyResource.builder()
//                .resourceName("dummy")
//                .content("updated")
//                .build();
//        var updateMerge = manager.plan(dummyUpdated, DummyResource.class);
//        var updatePlan = new Plan();
//        updatePlan.add(updateMerge);
//        manager.apply(updatePlan);
//
//        var dummy = javers.getLatestSnapshot("dummy", DummyResource.class).get();
//        var state = (Resource) JaversUtils.mapSnapshotToObject(dummy, DummyResource.class);
//        Assertions.assertEquals(dummyUpdated, state);
//
//        var cloud = provider.read(dummyUpdated);
//        Assertions.assertEquals(dummyUpdated, cloud);
//        Assertions.assertEquals(dummyUpdated, state);
//    }
//
//    /**
//     * Update resource property to null
//     */
//    @Test
//    void resourceUpdatePropertyNull() {
//        var provider = manager.getProvider(DummyResource.class);
//
//        var dummyResource = DummyResource.builder()
//                .resourceName("dummy")
//                .content("dummy")
//                .build();
//        var initMerge = manager.plan(dummyResource, DummyResource.class);
//        var initPlan = new Plan();
//        initPlan.add(initMerge);
//        manager.apply(initPlan);
//
//        var dummyUpdated = DummyResource.builder()
//                .resourceName("dummy")
//                .build();
//        var updateMerge = manager.plan(dummyUpdated, DummyResource.class);
//        var updatePlan = new Plan();
//        updatePlan.add(updateMerge);
//        manager.apply(updatePlan);
//
//        var dummy = javers.getLatestSnapshot("dummy", DummyResource.class).get();
//        var state = (Resource) JaversUtils.mapSnapshotToObject(dummy, DummyResource.class);
//        Assertions.assertEquals(dummyUpdated, state);
//
//        var cloud = provider.read(dummyUpdated);
//        Assertions.assertEquals(dummyUpdated, cloud);
//        Assertions.assertEquals(dummyUpdated, state);
//    }

}