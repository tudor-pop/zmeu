package dev.fangscl.Runtime.Functions.Numeric;

import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntFunctionTest extends BaseTest {
    private IntFunction function;

    @BeforeEach
    void init() {
        function = new IntFunction();
    }

    @Test
    void stringToInt() {
        var res = function.call(interpreter, List.of("1"));
        assertEquals(1, res);
    }

    @Test
    void floatToInt() {
        var res = function.call(interpreter, List.of(1.2f));
        assertEquals(1, res);
    }

    @Test
    void doubleToInt() {
        var res = function.call(interpreter, List.of(1.2d));
        assertEquals(1, res);
    }

    @Test
    void intToInt() {
        var res = function.call(interpreter, List.of(1));
        assertEquals(1, res);
    }

}