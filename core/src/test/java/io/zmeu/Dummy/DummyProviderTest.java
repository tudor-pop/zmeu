package io.zmeu.Dummy;

import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Persistence.ResourceRepository;
import io.zmeu.Plugin.Providers;
import io.zmeu.Resource.ResourceFactory;
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
        Assertions.assertNotNull(state); // check resource was saved in state
        Assertions.assertEquals(src, state);

        // a new run of the same resource name but with a different uuid (new code execution)
        // should look at the resource name instead of uuid since the uuid from src is different than the one
        // from state so the match should happen based on the name
        state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);


        // read from cloud
        var provider = manager.getProvider(DummyResource.class);
        var cloud = provider.read(src.getProperties());
        var cloudResource = ResourceFactory.from(src, cloud);
        Assertions.assertNotNull(cloud); // assert resource was saved in state
        Assertions.assertEquals(src, cloudResource);
        Assertions.assertEquals("""
                @|green +|@ resource DummyResource dummy {
                @|green +|@	arn     = null
                @|green +|@	color   = null
                @|green +|@	content = "some content"
                @|green +|@ }
                """.trim(), manager.changelog());
    }

    /**
     * resource exist in cloud but not in state and existing resource in src specifies an ARN for the cloud resource to be read
     * -> try implicit or explicit import or throw error if it can't be imported
     * show import intent in cli using the = sign in cyan color
     */
    @Test
    void addResourceToState() {
        var src = new Resource("dummy", DummyResource.builder()
                .content("some content")
                .build()
        );

        // create cloud resource
        var provider = manager.getProvider(src.getProperties().getClass());
        provider.create(src.getProperties());
        // check it was saved
        var cloud = (DummyResource) provider.read(src.getProperties());
        var cloudResource = ResourceFactory.from(src, cloud);
        Assertions.assertEquals(src, cloudResource);

        var state = repository.find(src);// nothing should be in state. We just have a cloud resource up until now
        Assertions.assertNull(state);

        // src resource using an ARN should import the cloud resource into state(db)
        var importedSrcByArn = new Resource("dummy", DummyResource.builder()
                .content("some content")
                .arn(cloud.getArn())
                .build()
        );
        importedSrcByArn.setExisting(true);
        var plan = manager.plan(importedSrcByArn);
        manager.apply(manager.toPlan(plan)); // save to plan but omit creating it again since it was imported

        // asert old src with generated ID can be retrieved from state
        state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);
        Assertions.assertEquals("""
                @|cyan =|@ resource DummyResource dummy {
                @|cyan =|@	arn     = "arn:1"
                @|cyan =|@	color   = null
                @|cyan =|@	content = "some content"
                @|cyan =|@ }
                """.trim(), manager.changelog());
    }

    @Test
    @DisplayName("src should update cloud property")
    void sameResourcePropertyChangeShouldUpdateCloudProperty() {
        var src = new Resource("src",
                DummyResource.builder()
                        .content("src")
                        .build());
        manager.apply(manager.toPlan(manager.plan(src))); // create cloud/state resource

        var provider = manager.getProvider(DummyResource.class);
        var cloud = (DummyResource) provider.read(src.getProperties());
        var cloudResource = ResourceFactory.from(src, cloud);
        Assertions.assertEquals(src, cloudResource);

        // same resource changes all property except arn
        src.setProperties(
                DummyResource.builder()
                        .content("new content")
                        .color("new color")
                        .arn(cloud.getArn())
                        .build()
        );

        var plan = manager.plan(src);
        Assertions.assertInstanceOf(ValueChange.class, plan.changes().get(0));
        manager.apply(manager.toPlan(plan));

        // asert old src with generated ID can be retrieved from state
        var state = manager.find(src);
        Assertions.assertNotNull(state); // assert resource was saved in state
        Assertions.assertEquals(src, state);

        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource src {
                	arn     = "arn:1"
                @|yellow ~|@	color   = null  @|yellow ->|@ "new color"
                @|yellow ~|@	content = "src" @|yellow ->|@ "new content"
                @|yellow ~|@ }
                """.trim(), manager.changelog());
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
        var cloud = provider.read(src.getProperties());
        var cloudResource = ResourceFactory.from(src, cloud);
        Assertions.assertEquals(src, cloudResource);

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