package io.zmeu.Dummy;

import io.zmeu.Base.JaversTest;
import io.zmeu.Diff.Diff;
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

@Log4j2
class DummyDiffTest extends JaversTest {
    private Diff diff;

    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff(javers, mapper);
    }

    @Test
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
    }

    @Test
    void addResourceToRemote() {
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
    }

    @Test
    void addResourceToLocal() {
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(expected, res.resource());
        // should be empty because the resource exists in cloud and in code but is missing in state so we just need to add it in state
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
    }

    @SneakyThrows
    @Test
    public void acceptSrc() {
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

        var res = this.diff.merge(localState, sourceState, remoteState);
        var expected = new Resource("main",
                DummyResource.builder()
                        .content("src")
                        .uid("cloud-id-random")
                        .build()
        );

        javers.processChangeList(res.changes(), new ResourceChangeLog(true));
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
    }

    @Test
    void srcChangesLocalAndRemote() {
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
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
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
        Assertions.assertEquals(new Resource(), res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ObjectRemoved.class, res.changes().get(0));

        Assertions.assertEquals("""
                @|red -|@ resource DummyResource main {
                @|red -|@	name   	= null
                @|red -|@	content	= "local"
                @|red -|@	uid    	= "cloud-id-random"
                @|red -|@ }
                """.trim(), log); // assert formatting remains intact
    }

    @Test
    void srcPropertyRemovalDeletesPropertyRemote() {
        var localState = new Resource("main",
                DummyResource.builder()
                        .content("local")
                        .uid("cloud-id-random")
                        .build()
        );

        var srcState = new Resource("main",
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

        var res = diff.merge(localState, srcState, cloudState);
        var log = javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        // optimise to always reduce the same empty resource during merge such that  res.resource() always points to the same instance instead of creating millions of empty resources
        Assertions.assertEquals(srcState, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));

        /*
        ~ resource DummyResource main {
            name    = null
            content = "local"
        -	uid     = "cloud-id-random" -> null
        ~ }
         */
        Assertions.assertEquals("""
                @|yellow ~|@ resource DummyResource main {
                	name    = null
                	content = "local"
                @|red -|@	uid     = "cloud-id-random" @|white ->|@ @|white null|@
                @|yellow ~|@ }
                """.trim(), log); // assert formatting remains intact
    }

}