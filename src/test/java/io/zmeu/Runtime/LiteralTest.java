package io.zmeu.Runtime;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Runtime.Values.NullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LiteralTest extends BaseTest {
    @Test
    void literal() {
        var res = interpreter.eval(10.0);
        Assertions.assertEquals(10.0, res);
    }

    @Test
    void literalDouble() {
        var res = interpreter.eval(10.1);
        Assertions.assertEquals(10.1, res);
    }

    @Test
    void integerLiteral() {
        var res = interpreter.eval(NumericLiteral.of(10));
        Assertions.assertEquals(10, res);
    }

    @Test
    void stringLiteral() {
        var res = interpreter.eval("""
                "hello world!"
                """);
        Assertions.assertEquals("\"hello world!\"\n", res);
    }

    @Test
    void stringLiterals() {
        var res = interpreter.eval("hello world!");
        Assertions.assertEquals("hello world!", res);
    }

    @Test
    void boolFalse() {
        var res = (Boolean) interpreter.eval(false);
        Assertions.assertFalse(res);
    }

    @Test
    void boolTrue() {
        var res = (Boolean) interpreter.eval(true);
        Assertions.assertTrue(res);
    }

    @Test
    void NullTest() {
        var res = (NullValue) interpreter.eval(new Identifier());
        Assertions.assertEquals(NullValue.of(), res.getRuntimeValue());
    }
}
