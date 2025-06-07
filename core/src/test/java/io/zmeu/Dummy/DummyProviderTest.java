package io.zmeu.Dummy;

import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Persistence.ResourceRepository;
import io.zmeu.Plugin.Providers;
import io.zmeu.Zmeufile.Dependencies;
import io.zmeu.Zmeufile.Zmeufile;
import io.zmeu.Resource.Resource;
import lombok.extern.log4j.Log4j2;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.jupiter.api.*;

import java.util.List;

@Log4j2
class DummyProviderTest extends JaversWithInterpreterTest {
    private ResourceManager manager;
    private Providers providers;
    private ResourceRepository repository;

    @BeforeEach
    void setUp() {
        providers = new Providers(new Zmeufile(new Dependencies(List.of())));
        var diff = new Diff(JaversFactory.createNoDb(), providers);
        repository = new ResourceRepository();
        manager = new ResourceManager(providers, gson, diff, repository);
        providers.loadPlugins();
        repository.deleteAll();
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
        // src with generated ID can be retrieved from state
        var state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        src.setId(state.getId());
        Assertions.assertEquals(src, state);

        // a new run of the same resource name but with a different uuid (new code execution)
        // should look at the resource name instead of uuid since the uuid from src is different than the one
        // from state so the match should happen based on the name
        state = manager.find(src);
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
        // asert old src with generated ID can be retrieved from state
        var state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);

        state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);
    }

    @Test
    @DisplayName("src should update cloud property")
    void srcShouldUpdateCloudProperty() {
        var src = new Resource("src",
                DummyResource.builder()
                        .content("src")
                        .build());
        repository.saveOrUpdate(src);
        var plan = manager.plan(src);
        manager.apply(manager.toPlan(plan)); // create cloud resource
        manager.refresh();

        var provider = manager.getProvider(DummyResource.class);
        var cloud = provider.read(src);
        Assertions.assertEquals(src, cloud);

        var newSrc = new Resource("src",
                DummyResource.builder()
                        .content("new content")
                        .color("new color")
                        .build());
        manager.refresh();
        plan = manager.plan(newSrc);
        Assertions.assertTrue(plan.changes().get(0) instanceof ValueChange);
        manager.apply(manager.toPlan(plan));

        // asert old src with generated ID can be retrieved from state
        var state = manager.find(newSrc);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);
    }

    @Test
    @DisplayName("src should delete cloud resource")
    void srcShouldDeleteCloudResource() {
        var src = new Resource("src",
                DummyResource.builder()
                        .content("src")
                        .build());
        var plan = manager.plan(src);
        manager.apply(manager.toPlan(plan)); // create cloud resource

        var provider = manager.getProvider(DummyResource.class);
        var cloud = provider.read(src);
        Assertions.assertEquals(src, cloud);

        var dummy = (DummyResource) src.getProperties();
        dummy.setContent("new content"); // change some content
        dummy.setColor("new color");
        plan = manager.plan(src);
        manager.apply(manager.toPlan(plan));

        // asert old src with generated ID can be retrieved from state
        var state = manager.find(src);
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