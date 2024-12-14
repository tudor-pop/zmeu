package io.zmeu.Diff;

import io.zmeu.Base.JaversTest;
import io.zmeu.Resources.TestResource;
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
class DiffTest extends JaversTest {
    private Diff diff;

    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff(javers, mapper);
    }

    @Test
    void noChanges() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var cloudState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var res = diff.merge(localState, sourceState, cloudState);
        var expected = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    void srcOverridesRemote() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var cloudState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var res = diff.merge(localState, sourceState, cloudState);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
    }

    @Test
    void addResourceToRemote() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var res = diff.merge(localState, sourceState, null);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        // should not be empty because the resource exists in src+state but is missing in cloud so we should create it while processing
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
    }

    @Test
    void addResourceToLocal() {
        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        var remoteState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        var res = diff.merge(null, sourceState, remoteState);
        var expected = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(expected, res.resource());
        // should be empty because the resource exists in cloud and in code but is missing in state so we just need to add it in state
        Assertions.assertTrue(res.changes().isEmpty());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
    void addResourceToLocalAndRemote() {
        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var res = diff.merge(null, sourceState, null);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(NewObject.class, res.changes().get(0));
    }

    @SneakyThrows
    @Test
    public void acceptSrc() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();


        var remoteState = TestResource.builder()
                .resourceName("main")
                .content("remote")
                .uid("cloud-id-random")
                .build();

        var res = this.diff.merge(localState, sourceState, remoteState);
        var expected = TestResource.builder()
                .resourceName("main")
                .content("src")
                .uid("cloud-id-random")
                .build();

        javers.processChangeList(res.changes(), new ResourceChangeLog(true));
        Assertions.assertEquals(expected, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class, res.changes().get(0));
    }


    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeResourcePropertiesFromSrc() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var srcState = TestResource.builder()
                .resourceName("main")
                .build();

        var remoteState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var res = diff.merge(localState, srcState, remoteState);
        var plan = TestResource.builder()
                .resourceName("main")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class,res.changes().get(0));
    }

    @Test
    void srcChangesLocalAndRemote() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var cloudState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();


        var res = diff.merge(localState, sourceState, cloudState);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(plan, res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ValueChange.class,res.changes().get(0));
    }
    @Test
    void srcRemovalDeletesRemote() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var cloudState = TestResource.builder()
                .resourceName("main")
                .content("local")
                .uid("cloud-id-random")
                .build();

        var res = diff.merge(localState, null, cloudState);
        javers.processChangeList(res.changes(), new ResourceChangeLog(true));

        Assertions.assertEquals(new TestResource(), res.resource());
        Assertions.assertFalse(res.changes().isEmpty());
        Assertions.assertInstanceOf(ObjectRemoved.class,res.changes().get(0));
    }

}