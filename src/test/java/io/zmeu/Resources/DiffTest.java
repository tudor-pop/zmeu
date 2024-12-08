package io.zmeu.Resources;

import io.zmeu.Diff.Diff;
import io.zmeu.Diff.JaversFactory;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.javers.core.Javers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

@Log4j2
class DiffTest {
    private Diff diff;

    @SneakyThrows
    @BeforeEach
    void init() {
        Javers javers = JaversFactory.createNoDb();
        diff = new Diff(javers, new ModelMapper());
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
        var plan = TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();
        Assertions.assertEquals(plan, res.resource());
    }

    @Test
    void sourceChangeOverridesRemote() {
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

        Assertions.assertEquals(plan, res.resource());
    }

    @Test
    void addClusterToRemote() {
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

        Assertions.assertEquals(plan, res.resource());
    }

    @Test
    void addClusterToLocal() {
        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        var remoteState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();
        var res = diff.merge(null, sourceState, remoteState);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        Assertions.assertEquals(plan, res.resource());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
    void addClusterToLocalAndRemote() {
        var sourceState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var res = diff.merge(null, sourceState, null);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        Assertions.assertEquals(plan, res.resource());
    }

    @SneakyThrows
    @Test
    public void acceptSrc() {
        var localState = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var sourceState = TestResource.builder()
                .resourceName("main")
                .build();


        var remoteState = TestResource.builder()
                .resourceName("main")
                .content("remote")
                .build();

        var res = this.diff.merge(localState, sourceState, remoteState);

        Assertions.assertEquals(res.resource(), sourceState);
    }


    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeClusterPropertiesFromSrc() {
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
        var plan =  TestResource.builder()
                .resourceName("main")
                .build();

        Assertions.assertEquals(plan, res.resource());
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState =  TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();

        var sourceState =  TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        var cloudState =  TestResource.builder()
                .resourceName("main")
                .content("local")
                .build();


        var res = diff.merge(localState, sourceState, cloudState);
        var plan = TestResource.builder()
                .resourceName("main")
                .content("src")
                .build();

        Assertions.assertEquals(plan, res.resource());
    }

}