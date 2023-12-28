package dev.fangscl.Diff;

import dev.fangscl.Backend.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
        var expected = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();
        printer.print(res);
        Assertions.assertEquals(diff.toJsonNode(expected), res.sourceCode());
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
        var expected = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(expected), res.sourceCode());
    }

    @Test
    void remoteChangeIsAddBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("clusterName", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("clusterName", "src"))
                .build();

        var res = diff.plan(localState, sourceState, null);
        var expected = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(expected), res.sourceCode());
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
        var expected = Resource.builder()
                .name("main")
                .properties(of("clusterName", "src"))
                .build();
        printer.print(res);

        Assertions.assertEquals(diff.toJsonNode(expected), res.sourceCode());
    }

}