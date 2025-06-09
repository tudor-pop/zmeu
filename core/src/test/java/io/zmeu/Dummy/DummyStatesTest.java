package io.zmeu.Dummy;

import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.Providers;
import io.zmeu.Resource.Resource;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Use cases:
 * 1. no changes between src, state and cloud
 * 2. src and local override cloud
 * 3. src overrides local and cloud
 * 4. src adds to local and cloud
 * 5. src does not override cloud generated id(immutable) but changes other cloud properties
 * 6. src changes local/cloud properties
 * 7. removed/missing resources from cloud should be added back if present in src
 * 8. removed resources from cloud should be added back if present in src and missing in local
 * 9. no state returns null
 * 10. deleting src property must delete local and remote
 * 11. remove src resource removes remote
 * 12. remove src property removes remote and local property
 * 13. resource renamed in src but not deleted/added - renaming detection
 * 14. replace resource on immutable value change
 * 15. resource rename should only happen between local and source
 * 16. replace resource on immutable value change and resource name
 */
@Log4j2
class DummyStatesTest extends JaversWithInterpreterTest {
    private Diff diff;
    private Providers providers;
    private ResourceChangeLog changeProcessor;


    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff(javers, providers);
        var provider = new DummyProvider();
        providers = new Providers();
        providers.putProvider(provider.schemasString(), provider);
        changeProcessor = new ResourceChangeLog(mapper);
    }

    /**
     * source code, state and cloud are consistent
     */
    @Test
    @DisplayName("no changes between src, state and cloud")
    void noChanges() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build());
        localState.setId(UUID.randomUUID()); // if local is present, must have an id which comes from the database

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

        var res = diff.merge(localState, sourceState, cloudState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        expected.setId(localState.getId());
        javers.processChangeList(res.changes(), changeProcessor);
        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    @DisplayName("src and local override cloud")
    void srcOverridesRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID()); // if local is present, must have an id which comes from the database

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("remote")
                        .build()
        );
        var res = diff.merge(localState, sourceState, cloudState);
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        plan.setId(sourceState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(plan, res.resource());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = null
                	color   = null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [DARK-GREY]null
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log)); // assert formatting remains intact
    }

    @Test
    @DisplayName("src overrides local and cloud")
    void srcAndLocalOverridesRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID()); // if local is present, must have an id which comes from the database
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("remote")
                        .build()
        );
        var res = diff.merge(localState, sourceState, cloudState);
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        plan.setId(sourceState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(plan, res.resource());
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = null
                	color   = null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [DARK-GREY]null
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log)); // assert formatting remains intact
    }

    @Test
    @DisplayName("src adds to local and cloud")
    void addResourceToLocalAndRemote() {
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(null, sourceState, null);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(expected.getId(), sourceState.getId());
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                + resource DummyResource main {
                +	arn     = null
                +	color   = null
                +	content = "src"
                + }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [GREEN]+[BLUE] resource DummyResource main {
                [GREEN]+	arn     = [DARK-GREY]null
                [GREEN]+	color   = [DARK-GREY]null
                [GREEN]+	content = [GREEN-STR]"src"
                [GREEN]+ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log)); // assert formatting remains intact
    }

    @SneakyThrows
    @Test
    @DisplayName("src does not override cloud generated id but changes other cloud properties")
    public void srcMergeWithCloudImmutable() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );


        var remoteState = new Resource("main",
                DummyResource.builder()
                        .content("remote")
                        .arn("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, sourceState, remoteState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .arn("cloud-id-random")
                        .build()
        );
        expected.setId(remoteState.getId());

        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));

        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = "cloud-id-random"
                	color   = null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [GREEN-STR]"cloud-id-random"
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("src changes local/cloud properties")
    void srcChangesLocalAndCloud() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );


        var res = diff.merge(localState, sourceState, cloudState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        expected.setId(localState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));

        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = null
                	color   = null
                ~	content = "local" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [DARK-GREY]null
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"local"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("src changes local/cloud properties")
    void srcChangesLocal() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );


        var res = diff.merge(localState, sourceState, cloudState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        expected.setId(localState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(localState.getId(), sourceState.getId());
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    @DisplayName("removed resources from cloud should be added back if present in src")
    void removedFromCloudGetsAddedBySrc() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(localState, sourceState, null);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        expected.setId(localState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(expected, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
        Assertions.assertEquals("""
                + resource DummyResource main {
                +	arn     = null
                +	color   = null
                +	content = "src"
                + }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [GREEN]+[BLUE] resource DummyResource main {
                [GREEN]+	arn     = [DARK-GREY]null
                [GREEN]+	color   = [DARK-GREY]null
                [GREEN]+	content = [GREEN-STR]"src"
                [GREEN]+ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("removed resources from cloud should be added back if present in src and missing in local")
    void removedFromCloudGetsAddedBySrcAndLocal() {
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(null, sourceState, null);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(expected, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
        Assertions.assertEquals("""
                + resource DummyResource main {
                +	arn     = null
                +	color   = null
                +	content = "src"
                + }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [GREEN]+[BLUE] resource DummyResource main {
                [GREEN]+	arn     = [DARK-GREY]null
                [GREEN]+	color   = [DARK-GREY]null
                [GREEN]+	content = [GREEN-STR]"src"
                [GREEN]+ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("no state returns null")
    void removedFromAllStates() {
        var res = diff.merge(null, null, null);
        javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertNull(res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    @DisplayName("deleting src property must delete local and remote")
    void removeResourcePropertiesFromSrc() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("main",
                DummyResource.builder()
                        .build()
        );

        var remoteState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(localState, srcState, remoteState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .build()
        );
        expected.setId(localState.getId());
        var log = javers.processChangeList(res.changes(), changeProcessor);

        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = null
                	color   = null
                -	content = "src" -> null
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [DARK-GREY]null
                	color   = [DARK-GREY]null
                [RED]-	content = "src" [DARK-GREY]-> null
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("remove src resource removes remote")
    void removeSrcResourceRemovesRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());
        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("remote") // note remote state will be shown in console instead of local state
                        .arn("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, null, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(cloudState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ObjectRemoved.class, res.changes().get(0));

        Assertions.assertEquals("""
                - resource DummyResource main {
                -	arn     = "cloud-id-random"
                -	color   = null
                -	content = "remote"
                - }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [RED]-[BLUE] resource DummyResource main {
                [RED]-	arn     = [GREEN-STR]"cloud-id-random"
                [RED]-	color   = [DARK-GREY]null
                [RED]-	content = [GREEN-STR]"remote"
                [RED]- }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("remove src property removes remote and local property ")
    void removeSrcPropertyRemovesRemoteProperty() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .color("local")
                        .arn("cloud-id-random")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .color("local")
                        .content("remote")
                        .arn("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));


        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .arn("cloud-id-random")
                        .build()
        );
        expected.setId(localState.getId());
        Assertions.assertEquals(localState.getId(), srcState.getId());
        // assert that:
        // 1. color was removed
        // 2. content is "src"
        // 3. cloud immutable property was maintained
        Assertions.assertEquals(expected, res.resource());

        /*
        ~ resource DummyResource main {
        -	color    = "local"  -> null
        ~	content = "remote" -> "src"
            arn     = "cloud-id-random"
        ~ }
         */
        Assertions.assertEquals("""
                ~ resource DummyResource main {
                	arn     = "cloud-id-random"
                -	color   = "local"  -> null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main {
                	arn     = [GREEN-STR]"cloud-id-random"
                [RED]-	color   = "local"  [DARK-GREY]-> null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("resource renamed in src but not deleted/added - renaming detection")
    void renameDetection() {
        // resource was already added so local state has a stable/immutable ID (arn=cloud-id-random)
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .color("local")
                        .arn("cloud-id-random")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("newName",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .color("local")
                        .content("remote")
                        .arn("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));


        var expected = new Resource("newName",
                DummyResource.builder()
                        .content("src")
                        .arn("cloud-id-random")
                        .build()
        );
        expected.setId(localState.getId());
        expected.getIdentity().setRenamedFrom(localState.getIdentity().getRenamedFrom());
        Assertions.assertEquals(localState.getId(), srcState.getId());
        // assert that:
        // 1. color was removed
        // 2. content is "src"
        // 3. cloud immutable property was maintained
        Assertions.assertEquals(expected, res.resource());

        /*
            ~ resource DummyResource main -> newName {
            -	name    = "local"	-> null
            ~	content = "remote"	-> "src"
                arn     = "cloud-id-random"
            ~ }
         */
        Assertions.assertEquals("""
                ~ resource DummyResource main -> newName {
                	arn     = "cloud-id-random"
                -	color   = "local"  -> null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main [MAGENTA]-> newName {
                	arn     = [GREEN-STR]"cloud-id-random"
                [RED]-	color   = "local"  [DARK-GREY]-> null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("resource rename should only happen between local and source")
    void renameDetectionShouldOnlyHappenBetweenLocalAndSource() {
        // resource was already added so local state has a stable/immutable ID (arn=cloud-id-random)
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .color("local")
                        .arn("cloud-id-random")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("newName",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("remote",
                DummyResource.builder()
                        .color("local")
                        .content("remote")
                        .arn("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));


        var expected = new Resource("newName",
                DummyResource.builder()
                        .content("src")
                        .arn("cloud-id-random")
                        .build()
        );
        expected.setId(localState.getId());
        expected.getIdentity().setRenamedFrom(localState.getIdentity().getRenamedFrom());
        Assertions.assertEquals(localState.getId(), srcState.getId());
        // assert that:
        // 1. name was removed
        // 2. content is "src"
        // 3. cloud immutable property was maintained
        Assertions.assertEquals(expected, res.resource());

        /*
            ~ resource DummyResource main -> newName {
            -	name    = "local"	-> null
            ~	content = "remote"	-> "src"
                arn     = "cloud-id-random"
            ~ }
         */
        Assertions.assertEquals("""
                ~ resource DummyResource main -> newName {
                	arn     = "cloud-id-random"
                -	color   = "local"  -> null
                ~	content = "remote" -> "src"
                ~ }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [YELLOW]~[BLUE] resource DummyResource main [MAGENTA]-> newName {
                	arn     = [GREEN-STR]"cloud-id-random"
                [RED]-	color   = "local"  [DARK-GREY]-> null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [YELLOW]~ }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("replace resource on immutable value change")
    void replaceDetection() {
        // resource was already added so local state has a stable/immutable ID (arn=cloud-id-random)
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .arn("immutable-change")
                        .build()
        );

        var cloudState = new Resource("unknown",
                DummyResource.builder()
                        .content("remote")
                        .arn("immutable")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));


        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .arn("immutable-change")
                        .build()
        );
        expected.setId(localState.getId());
        expected.setReplace(localState.getReplace());
        Assertions.assertEquals(localState.getId(), srcState.getId());
        // assert that:
        // 1. name was removed
        // 2. content is "src"
        // 3. cloud immutable property was maintained
        Assertions.assertEquals(expected, res.resource());

        /*
            ± resource DummyResource main {
                name    = null
            ~	content = "remote"    -> "src"
            ±	arn     = "immutable" -> "immutable-change"
            ± }
         */
        Assertions.assertEquals("""
                ± resource DummyResource main { # marked for replace
                ±	arn     = "immutable" -> "immutable-change"
                	color   = null
                ~	content = "remote" -> "src"
                ± }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [MAGENTA]±[BLUE] resource DummyResource main {[MAGENTA] # marked for replace
                [MAGENTA]±	arn     = [MAGENTA]"immutable"[MAGENTA] -> "immutable-change"
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [MAGENTA]± }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }

    @Test
    @DisplayName("replace resource on immutable value change and resource name")
    void replaceDetectionAndResourceName() {
        // resource was already added so local state has a stable/immutable ID (arn=cloud-id-random)
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );
        localState.setId(UUID.randomUUID());

        var srcState = new Resource("newMain",
                DummyResource.builder()
                        .content("src")
                        .arn("immutable-change")
                        .build()
        );

        var cloudState = new Resource("unknown",
                DummyResource.builder()
                        .content("remote")
                        .arn("immutable")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), changeProcessor);

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));


        var expected = new Resource("newMain",
                DummyResource.builder()
                        .content("src")
                        .arn("immutable-change")
                        .build()
        );
        expected.setId(localState.getId());
        expected.setReplace(localState.getReplace());
        expected.getIdentity().setRenamedFrom(localState.getIdentity().getRenamedFrom());
        Assertions.assertEquals(localState.getId(), srcState.getId());
        // assert that:
        // 1. name was removed
        // 2. content is "src"
        // 3. cloud immutable property was maintained
        Assertions.assertEquals(expected, res.resource());

        /*
            ± resource DummyResource main {
                name    = null
            ~	content = "remote"    -> "src"
            ±	arn     = "immutable" -> "immutable-change"
            ± }
         */
        Assertions.assertEquals("""
                ± resource DummyResource main -> newMain { # marked for replace
                ±	arn     = "immutable" -> "immutable-change"
                	color   = null
                ~	content = "remote" -> "src"
                ± }
                """.trim(), ResourceChangeLog.stripAnsi(log));
        Assertions.assertEquals("""
                [MAGENTA]±[BLUE] resource DummyResource main [MAGENTA]-> newMain {[MAGENTA] # marked for replace
                [MAGENTA]±	arn     = [MAGENTA]"immutable"[MAGENTA] -> "immutable-change"
                	color   = [DARK-GREY]null
                [YELLOW]~	content = [YELLOW]"remote"[YELLOW] -> "src"
                [MAGENTA]± }
                """.trim(), ResourceChangeLog.tokenizeAnsi(log));
    }


    /**
     * Got source + cloud
     */
    // todo implement import
//    @Test
//    void importResourceToState() {
//        var sourceState = new Resource("main",
//                DummyResource.builder()
//                        .content("src")
//                        .build()
//        );
//        var remoteState = new Resource("main",
//                DummyResource.builder()
//                        .content("src")
//                        .build()
//        );
//        var res = diff.merge(null, sourceState, remoteState);
//        var expected = new Resource("main",
//                DummyResource.builder()
//                        .content("src")
//                        .build());
//        var log = javers.processChangeList(res.changes(), changeProcessor);
//
//        Assertions.assertEquals(expected, res.resource());
//        // should be empty because the resource exists in cloud and in code but is missing in state so we just need to add it in state
//        Assertions.assertTrue(res.changes().isEmpty());
//        Assertions.assertEquals("""
//                @|green +|@ resource DummyResource main {
//                @|green +|@	name    = null
//                @|green +|@	content = "src"
//                @|green +|@	arn     = null
//                @|green +|@ }
//                """.trim(), log); // assert formatting remains intact
//    }

}