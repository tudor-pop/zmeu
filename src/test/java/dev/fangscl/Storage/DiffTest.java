package dev.fangscl.Storage;

import dev.fangscl.Runtime.Environment.Environment;
import dev.fangscl.Runtime.Values.ResourceValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class DiffTest {
    private Diff diff;

    @BeforeEach
    void init() {
        diff = new Diff();
    }

    @Test
    void noChanges() {
        var localState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var sourceState = ResourceValue.builder().name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var cloudState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        Assertions.assertEquals(expected, res);
    }
    @Test
    void sourceChangeOverridesRemote() {
        var localState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var sourceState = ResourceValue.builder().name("main")
                .args(new Environment(Map.of("name", "src")))
                .build();

        var cloudState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "src")))
                .build();

        Assertions.assertEquals(expected, res);
    }
    @Test
    void remoteChangeIsIgnored() {
        var localState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var sourceState = ResourceValue.builder().name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var cloudState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "remote")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        Assertions.assertEquals(expected, res);
    }

    @Test
    void remoteChangeIsOverriddenBySrc() {
        var localState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local")))
                .build();

        var sourceState = ResourceValue.builder().name("main")
                .args(new Environment(Map.of("name", "src")))
                .build();

        var cloudState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "remote")))
                .build();


        var res = diff.patch(localState, sourceState, cloudState);
        var expected = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "src")))
                .build();

        Assertions.assertEquals(expected, res);
    }

}