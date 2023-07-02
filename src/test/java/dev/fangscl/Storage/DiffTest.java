package dev.fangscl.Storage;

import dev.fangscl.Runtime.Environment.Environment;
import dev.fangscl.Runtime.Values.ResourceValue;
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
    void test() {
        var localState = ResourceValue.builder()
                .name("main")
                .args(new Environment(Map.of("name", "local","size", "small")))
                .build();

        var sourceState = ResourceValue.builder().name("main")
                .args(new Environment(Map.of("name", "source")))
                .build();
        var cloudState = ResourceValue.of("main", new Environment(Map.of("name", "local","size", "small")));
        diff.patch(localState, sourceState, cloudState);
    }

}