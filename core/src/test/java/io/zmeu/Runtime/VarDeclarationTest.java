package io.zmeu.Runtime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class VarDeclarationTest extends BaseRuntimeTest {


    @Test
    void varNull() {
        var res = eval("var x");
        assertNull(res);
        Assertions.assertTrue(global.hasVar("x"));
        assertNull(global.get("x"));
        log.info(toJson(res));
    }

    @Test
    void varInt() {
        var res = eval("var x = 2");
        assertEquals(2, res);
        log.info(toJson(res));
    }


    @Test
    void varDecimal() {
        var res = (Double) eval("var x = 2.1");
        assertEquals(2.1, res);
        log.info(toJson(res));
    }

    @Test
    void varBool() {
        var res = (Boolean) eval("var x = true");
        assertTrue(res);
        log.info(toJson(res));
    }


    @Test
    void varExpressionPlus() {
        var res = eval("var x = 2+2");
        assertEquals(4, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMinus() {
        var res = eval("var x = 2-2");
        assertEquals(0, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMultiplication() {
        var res = eval("var x = 2*2");
        assertEquals(4, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionDivision() {
        var res = eval("var x = 2/2");
        assertEquals(1, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBoolean() {
        var res = eval("var x = 2==2");
        var expected = true;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBooleanFalse() {
        var res = eval("var x = 2==1");
        var expected = false;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varMultiDeclaration() {
        var res = eval("""
                {
                    var y = 0
                    y=1
                }
                """);

        log.info(toJson(res));
        assertEquals(1, res);
    }

}
