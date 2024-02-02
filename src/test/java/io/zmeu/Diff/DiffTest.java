package io.zmeu.Diff;

import io.zmeu.file.FileResource;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Log4j2
class DiffTest {
    private Diff diff;

    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
    }

    @Test
    void noChanges() {
        var localState = FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();

        var sourceState = FileResource.builder().name("main").id("main")
                .content("local")
                .build();

        var cloudState = FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();

        var res = diff.plan(localState, sourceState, cloudState);
        var plan = FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();
        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void sourceChangeOverridesRemote() {
        var localState = FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();

        var sourceState = FileResource.builder().id("main")
                .name("main")
                .content("src")
                .build();

        var cloudState = FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();

        var res = diff.plan(localState, sourceState, cloudState);
        var plan = FileResource.builder().id("main")
                .name("main")
                .content("src")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addClusterToRemote() {
        var localState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var sourceState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var res = diff.plan(localState, sourceState, null);
        var plan = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addClusterToLocal() {
        var sourceState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();
        var remoteState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();
        var res = diff.plan(null, sourceState, remoteState);
        var plan = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
    void addClusterToLocalAndRemote() {
        var sourceState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var res = diff.plan(null, sourceState, null);
        var plan = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    /**
     * Overrides local state with remote state and then compares local state with src state
     */
    @SneakyThrows
    @Test
    public void shouldDo3WayDiff() {
        var localState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var sourceState = FileResource.builder()
                .id("main")
                .name("main")
                .build();


        var remoteState = FileResource.builder()
                .id("main")
                .name("main")
                .content("remote")
                .build();

//        var commit = javers.commit("tudor", remoteState);
        var res = this.diff.plan(localState, sourceState, remoteState);

        //then
        Assertions.assertEquals(res.sourceCode(), this.diff.getMapper().valueToTree(sourceState));
    }

    @SneakyThrows
    @Test
    public void shouldApply() {
        var localState = FileResource.builder()
                .name("main")
                .id("main")
                .content("src")
                .build();

        var sourceState = FileResource.builder()
                .name("main")
                .id("main")
                .build();

        var remoteState = FileResource.builder()
                .name("main")
                .id("main")
                .content("remote")
                .build();
//        var commit = javers.commit("tudor", remoteState);
        var res = this.diff.apply(localState, sourceState, remoteState);

        //then
        Assertions.assertEquals(res.sourceCode(), this.diff.getMapper().valueToTree(sourceState));
    }

    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeClusterPropertiesFromSrc() {
        var localState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var srcState = FileResource.builder()
                .id("main")
                .name("main")
                .build();

        var remoteState = FileResource.builder()
                .id("main")
                .name("main")
                .content("src")
                .build();

        var res = diff.plan(localState, srcState, remoteState);
        var plan =  FileResource.builder().id("main")
                .name("main")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState =  FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();

        var sourceState =  FileResource.builder().name("main").id("main")
                .content("src")
                .build();

        var cloudState =  FileResource.builder().id("main")
                .name("main")
                .content("local")
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = FileResource.builder().id("main")
                .name("main")
                .content("src")
                .build();

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

}