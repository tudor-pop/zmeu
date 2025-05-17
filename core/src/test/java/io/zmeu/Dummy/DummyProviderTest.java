package io.zmeu.Dummy;

import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Import.Dependencies;
import io.zmeu.Import.Zmeufile;
import io.zmeu.Plugin.Providers;
import io.zmeu.api.resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Log4j2
class DummyProviderTest extends JaversWithInterpreterTest {
    private ResourceManager manager;
    private Providers providers;

    @BeforeEach
    void setUp() {
        providers = new Providers(new Zmeufile(new Dependencies(List.of())));
        var diff = new Diff(JaversFactory.createH2File(), mapper, providers);
        manager = new ResourceManager(providers, gson, diff);
        providers.loadPlugins();
    }

    @AfterEach
    void tearDown() {
        providers.stopPlugins();
    }

    /**
     * resource doesn't exist in cloud or state. Should be created/added
     */
    @Test
    void resourceMissingCloudAndState() {
        var dummyResource = DummyResource.builder()
                .content("some content")
                .build();
        var src = new Resource("dummy", dummyResource);

        var plan = manager.plan(src);
        Assertions.assertTrue(plan.isNewResource()); // if true, resource should be added to both state and cloud

        // apply to state
        manager.apply(manager.toPlan(plan));
        var state = manager.findByResourceName("dummy");
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);

        // read from cloud
        var provider = manager.getProvider(DummyResource.class);
        var cloud = provider.read(src);
        Assertions.assertNotNull(cloud); // assert resource was saved in state
        Assertions.assertEquals(src, cloud);
    }

    /**
     * resource exist in src and cloud but not in state -> try implicit or explicit import or throw error if it can't be imported
     * show import intent in cli
     */
    @Test
    void importToJaversFromCloudShouldFailWithoutImport() {
        var dummyResource = DummyResource.builder()
                .content("some content")
                .build();
        var src = new Resource("dummy", dummyResource);

        var plan = manager.plan(src);
        Assertions.assertTrue(plan.isNewResource()); // if true, resource should be added to both state and cloud
        // simulate existing resource in cloud
        var provider = manager.getProvider(DummyResource.class);
        provider.create(src); // can be any resource. We just use the existing one
        var cloud = provider.read(src);
        Assertions.assertEquals(src, cloud);

        manager.apply(manager.toPlan(plan));
        var state = manager.findByResourceName("dummy");
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);

    }

//    /**
//     * resource exist in src and state but not in cloud should show cli output as ADD operation
//     */
//    @Test
//    void resourceExistsSrcAndJaversMissingCloud() {
//        var resource = new Resource(
//                "dummy",
//                DummyResource.builder()
//                        .content("dummy")
//                        .build()
//        );
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