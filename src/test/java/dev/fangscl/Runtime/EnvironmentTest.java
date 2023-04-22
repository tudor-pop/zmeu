package dev.fangscl.Runtime;

import com.google.gson.Gson;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentTest {
    private Gson gson = new Gson();
    private Environment environment;

    @BeforeEach
    void init() {
        environment = new Environment();
    }

    @Test
    void declareVar() {
        var res = environment.declareVar("x", 1);
        RuntimeValue expected = IntegerValue.of(1);
        Assertions.assertEquals(expected, res);
        Assertions.assertEquals(expected, environment.getVariables().get("x"));
    }

}
