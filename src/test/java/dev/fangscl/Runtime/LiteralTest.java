package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.Values.NullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LiteralTest extends BaseTest {
    @Test
    void literal() {
        var evalRes = interpreter.eval(10);
        Assertions.assertEquals(10, evalRes.getRuntimeValue());
    }

    @Test
    void literalDouble() {
        var evalRes = interpreter.eval(10.1);
        Assertions.assertEquals(10.1, evalRes.getRuntimeValue());
    }

    @Test
    void integerLiteral() {
        var evalRes = interpreter.eval(NumericLiteral.of(10));
        Assertions.assertEquals(10, evalRes.getRuntimeValue());
    }

    @Test
    void stringLiteral() {
        var evalRes = interpreter.eval("""
                "hello world!"
                """);
        Assertions.assertEquals("hello world!", evalRes.getRuntimeValue());
    }

    @Test
    void stringLiterals() {
        var evalRes = interpreter.eval("hello world!");
        Assertions.assertEquals("hello world!", evalRes.getRuntimeValue());
    }

    @Test
    void boolFalse() {
        var evalRes = interpreter.eval(false);
        Assertions.assertFalse(evalRes.getRuntimeValue());
    }

    @Test
    void boolTrue() {
        var evalRes = interpreter.eval(true);
        Assertions.assertTrue(evalRes.getRuntimeValue());
    }

    @Test
    void NullTest() {
        var evalRes = interpreter.eval(new Identifier());
        Assertions.assertEquals(NullValue.of(), evalRes.getRuntimeValue());
    }
}
