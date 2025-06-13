package io.zmeu.Runtime;

import io.zmeu.Runtime.exceptions.InvalidInitException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class ValDeclarationTest extends BaseRuntimeTest {


    @Test
    void valNull() {
        Assertions.assertThrows(InvalidInitException.class, () -> eval("val x"));
    }

    @Test
    void varInt() {
        var res = eval("val x = 2");
        assertEquals(2, res);
        log.info(toJson(res));
    }


    @Test
    void varDecimal() {
        var res = (Double) eval("val x = 2.1");
        assertEquals(2.1, res);
        log.info(toJson(res));
    }

    @Test
    void varBool() {
        var res = (Boolean) eval("val x = true");
        assertTrue(res);
        log.info(toJson(res));
    }


    @Test
    void varExpressionPlus() {
        var res = eval("val x = 2+2");
        assertEquals(4, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMinus() {
        var res = eval("val x = 2-2");
        assertEquals(0, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMultiplication() {
        var res = eval("val x = 2*2");
        assertEquals(4, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionDivision() {
        var res = eval("val x = 2/2");
        assertEquals(1, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBoolean() {
        var res = eval("val x = 2==2");
        var expected = true;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBooleanFalse() {
        var res = eval("val x = 2==1");
        var expected = false;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varMultiDeclaration() {
        var res = eval("""
                {
                    val y = 0
                    y=1
                }
                """);

        log.info(toJson(res));
        assertEquals(1, res);
    }

}
