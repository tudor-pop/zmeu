package dev.fangscl.Diff;

import dev.fangscl.Backend.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Map.of;

class DiffTest {
    private Diff diff;

    @BeforeEach
    void init() {
        diff = new Diff();
    }

    @Test
    void noChanges() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("state", "local"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();


        var res = diff.apply(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();

        Assertions.assertEquals(diff.toJsonNode(expected), res);
    }

    @Test
    void sourceChangeOverridesRemote() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("state", "src"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();


        var res = diff.apply(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties(of("state", "src"))
                .build();

        Assertions.assertEquals(diff.toJsonNode(expected), res);
    }

    @Test
    void remoteChangeIsAddBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("state", "src"))
                .build();

        var res = diff.apply(localState, sourceState, null);
        var expected = Resource.builder()
                .name("main")
                .properties(of("state", "src"))
                .build();

        Assertions.assertEquals(diff.toJsonNode(expected), res);
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties(of("state", "local", "hidden_state", "secret"))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties(of("state", "src"))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(of("state", "local"))
                .build();


        var res = diff.apply(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties(of("state", "src", "hidden_state", "secret"))
                .build();

        Assertions.assertEquals(diff.toJsonNode(expected), res);
    }

}