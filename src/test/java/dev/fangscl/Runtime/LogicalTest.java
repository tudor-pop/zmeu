package dev.fangscl.Runtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogicalTest extends BaseTest {

    @Test
    void trueOrTrue() {
        var res = (Boolean) eval("true || true");
        Assertions.assertTrue(res);
    }

    @Test
    void trueOrFalse() {
        // true || false -> true
        var res = (Boolean) eval("true || false");
        Assertions.assertTrue(res);
    }

    @Test
    void falseOrTrue() {
        // false || true -> true
        var res = (Boolean) eval("false || true");
        Assertions.assertTrue(res);
    }

    @Test
    void falseOrFalse() {
        // false || false -> true
        var res = (Boolean) eval("false || false");
        Assertions.assertFalse(res);
    }

    @Test
    void falseAndTrue() {
        // null && 2 -> null
        var res = (Boolean) eval("null && 2");
        Assertions.assertNull(res);
    }

    @Test
    void trueAndFalse() {
        // 2 && null -> null
        var res = (Boolean) eval("2 && null");
        Assertions.assertNull(res);
    }

    @Test
    void trueAndTrue() {
        // 1 && 2 -> 2
        var res = eval("1 && 2");
        Assertions.assertEquals(2, res);
    }

    @Test
    void falseAndFalse() {
        // false && false -> false
        var res = (Boolean) eval("false && false");
        Assertions.assertFalse(res);
    }

}
