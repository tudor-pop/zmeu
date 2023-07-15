package dev.fangscl.Diff;

import dev.fangscl.Backend.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
                .properties((Map.of("state", "local")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "local")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void sourceChangeOverridesRemote() {
        var localState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "src")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "src")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void remoteChangeIsIgnored() {
        var localState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "local")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "remote")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void remoteChangeIsOverriddenBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "src")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "remote")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "src")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void remoteChangeIsAddBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "src")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties(Map.of())
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "src")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void localHiddenIsNotRemovedBySrc() {
        var localState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local", "hidden_state", "secret")))
                .build();

        var sourceState = Resource.builder().name("main")
                .properties((Map.of("state", "src")))
                .build();

        var cloudState = Resource.builder()
                .name("main")
                .properties((Map.of("state", "local")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = Resource.builder()
                .name("main")
                .properties((Map.of("state", "src", "hidden_state", "secret")))
                .build();

        Assertions.assertEquals(expected, res);
    }

}