package io.zmeu.Runtime.Functions.Cast;

import io.zmeu.Runtime.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecimalCastFunctionTest extends BaseTest {
    private final DecimalCastFunction function = new DecimalCastFunction();

    @Test
    void stringToInt() {
        var res = function.call(interpreter, "1.1");
        assertEquals(1.1, res);
    }

    @Test
    void floatToInt() {
        var res = function.call(interpreter, 1.2f);
        assertEquals(1.2, res);
    }

    @Test
    void doubleToInt() {
        var res = function.call(interpreter, 1.2d);
        assertEquals(1.2, res);
    }

    @Test
    void intToInt() {
        var res = function.call(interpreter, 1);
        assertEquals(1.0, res);
    }

}