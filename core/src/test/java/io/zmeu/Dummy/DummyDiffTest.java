package io.zmeu.Dummy;

import io.zmeu.Base.JaversTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Plugin.Providers;
import io.zmeu.api.resource.Resource;
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

/**
 * Use cases:
 * 1. no changes between src, state and cloud
 * 2. src and local override cloud
 * 3. src overrides local and cloud
 * 4. src adds to local and cloud
 * 5. src does not override cloud generated id but changes other cloud properties
 * 6. src changes local/cloud properties
 * 7. removed/missing resources from cloud should be added back if present in src
 * 8. removed resources from cloud should be added back if present in src and missing in local
 */
@Log4j2
class DummyDiffTest extends JaversTest {
    private Diff diff;
    private Providers providers ;


    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff(javers);
        var provider = new DummyProvider();
        providers = new Providers();
        providers.putProvider(provider.schemasString(), provider);
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));
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
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(false));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                @|yellow ~|@	content = "local" @|white ->|@ "src"
                	uid     = null
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("src overrides local and cloud")
    void srcAndLocalOverridesRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

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
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(false));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                @|yellow ~|@	content = "local" @|white ->|@ "src"
                	uid     = null
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("src adds to local and cloud")
    void addResourceToLocalAndRemote() {
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(null, sourceState, null);
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertEquals(sourceState, res.resource());
        Assertions.assertEquals("""
                @|green +|@ resource DummyResource main {
                @|green +|@	name    = null
                @|green +|@	content = "src"
                @|green +|@	uid     = null
                @|green +|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @SneakyThrows
    @Test
    @DisplayName("src does not override cloud generated id but changes other cloud properties")
    public void srcMergeWithCloudReadonly() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );


        var remoteState = new Resource("main",
                DummyResource.builder()
                        .content("remote")
                        .uid("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, sourceState, remoteState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .uid("cloud-id-random")
                        .build()
        );

        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));

        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                @|yellow ~|@	content = "remote" @|white ->|@ "src"
                	uid     = "cloud-id-random"
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("src changes local/cloud properties")
    void srcChangesLocalAndCloud() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

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
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));

        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                @|yellow ~|@	content = "local" @|white ->|@ "src"
                	uid     = null
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("src changes local/cloud properties")
    void srcChangesLocal() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

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
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
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

        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(localState, sourceState, null);
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
        Assertions.assertEquals("""
                @|green +|@ resource DummyResource main {
                @|green +|@	name    = null
                @|green +|@	content = "src"
                @|green +|@	uid     = null
                @|green +|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("removed resources from cloud should be added back if present in src and missing in local")
    void removedFromCloudGetsAddedBySrcAndLocal() {
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(null, sourceState, null);
        var plan = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
        Assertions.assertEquals("""
                @|green +|@ resource DummyResource main {
                @|green +|@	name    = null
                @|green +|@	content = "src"
                @|green +|@	uid     = null
                @|green +|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeResourcePropertiesFromSrc() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var srcState = new Resource("main",
                DummyResource.builder()
                        .build()
        );

        var remoteState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());

        var res = diff.merge(localState, srcState, remoteState);
        var plan = new Resource("main",
                DummyResource.builder()
                        .build()
        );
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                @|red -|@	content = "src" @|white ->|@ @|white null|@
                	uid     = null
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    void srcRemovalDeletesRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .uid("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, null, cloudState);
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(cloudState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ObjectRemoved.class, res.changes().get(0));

        Assertions.assertEquals("""
                @|red -|@ resource DummyResource main {
                @|red -|@	name    = null
                @|red -|@	content = "local"
                @|red -|@	uid     = "cloud-id-random"
                @|red -|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    void srcPropertyRemovalDeletesPropertyRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .name("local")
                        .uid("cloud-id-random")
                        .build()
        );

        var srcState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );

        var cloudState = new Resource("main",
                DummyResource.builder()
                        .name("local")
                        .content("local")
                        .uid("cloud-id-random")
                        .build()
        );

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
        // assert that:
        // 1. name was removed
        // 2. content is "src"
        // 3. cloud readonly property was maintained
        Assertions.assertEquals(res.resource().getResource(), localState.getResource());

        /*
        ~ resource DummyResource main {
        -	name    = "local" -> null
        ~	content = "local" -> "src"
            uid     = "cloud-id-random"
        ~ }
         */
        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                @|red -|@	name    = "local" @|white ->|@ @|white null|@
                @|yellow ~|@	content = "local" -> "src"
                	uid     = "cloud-id-random"
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }


    /**
     * Got source + cloud
     */
    // todo implement import
    @Test
    void importResourceToState() {
        var sourceState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var remoteState = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build()
        );
        var res = diff.merge(null, sourceState, remoteState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .build());
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(expected, res.resource());
        // should be empty because the resource exists in cloud and in code but is missing in state so we just need to add it in state
        Assertions.assertTrue(res.changes().isEmpty());
        Assertions.assertEquals("""
                @|green +|@ resource DummyResource main {
                @|green +|@	name    = null
                @|green +|@	content = "src"
                @|green +|@	uid     = null
                @|green +|@ }
                """.trim(), log); // assert formatting remains intact
    }

}