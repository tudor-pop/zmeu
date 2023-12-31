package dev.fangscl.Diff;

import dev.fangscl.Backend.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Map.of;

class DiffTest {
    private Diff diff;
    private final Printer printer = new Printer();

    @BeforeEach
    void init() {
        diff = new Diff();
    }

    @Test
    void noChanges() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "local"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();
        printer.print(res);
        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void sourceChangeOverridesRemote() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addResourceToRemote() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();

        var res = diff.plan(localState, sourceState, null);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void addResourceToLocal() {
        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();
        var remoteState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        var res = diff.plan(null, sourceState, remoteState);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    @DisplayName("First apply creates remote and local states")
    void addResourceToLocalAndRemote() {
        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();

        var res = diff.plan(null, sourceState, null);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    @DisplayName("Deleting src must delete local and remote regardless of their state")
    void removeResourcePropertiesFromSrc() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        var srcState = Resource.builder()
                .name("main")
                .properties(of())
                .build();
        var remoteState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        var res = diff.plan(localState, srcState, remoteState);
        var plan = Resource.builder()
                .name("main")
                .properties(of())
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local", "hidden", "secret"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();


        var res = diff.plan(localState, sourceState, cloudState);
        var plan = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(plan), res.sourceCode());
    }

}