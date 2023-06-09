package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.Values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LiteralTest extends BaseTest {
    @Test
    void literal() {
        var evalRes = (DecimalValue)interpreter.eval(10.0);
        Assertions.assertEquals(10.0, evalRes.getRuntimeValue());
    }

    @Test
    void literalDouble() {
        var evalRes = (DecimalValue)interpreter.eval(10.1);
        Assertions.assertEquals(10.1, evalRes.getRuntimeValue());
    }

    @Test
    void integerLiteral() {
        var evalRes = (IntegerValue) interpreter.eval(NumericLiteral.of(10));
        Assertions.assertEquals(10, evalRes.getRuntimeValue());
    }

    @Test
    void stringLiteral() {
        var evalRes = (StringValue) interpreter.eval("""
                "hello world!"
                """);
        Assertions.assertEquals("hello world!", evalRes.getRuntimeValue());
    }

    @Test
    void stringLiterals() {
        var evalRes = (StringValue) interpreter.eval("hello world!");
        Assertions.assertEquals("hello world!", evalRes.getRuntimeValue());
    }

    @Test
    void boolFalse() {
        var evalRes = (BooleanValue)interpreter.eval(false);
        Assertions.assertFalse(evalRes.getRuntimeValue());
    }

    @Test
    void boolTrue() {
        var evalRes = (BooleanValue)interpreter.eval(true);
        Assertions.assertTrue(evalRes.getRuntimeValue());
    }

    @Test
    void NullTest() {
        var evalRes = (NullValue) interpreter.eval(new Identifier());
        Assertions.assertEquals(NullValue.of(), evalRes.getRuntimeValue());
    }
}
