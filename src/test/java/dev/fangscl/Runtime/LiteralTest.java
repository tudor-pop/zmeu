package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.Values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LiteralTest extends BaseTest {
    @Test
    void literal() {
        var evalRes = interpreter.eval(10);
        Assertions.assertEquals(10, ((IntegerValue) evalRes).getValue());
    }

    @Test
    void literalDouble() {
        var evalRes = interpreter.eval(10.1);
        Assertions.assertEquals(10.1, ((DecimalValue) evalRes).getValue());
    }

    @Test
    void integerLiteral() {
        var evalRes = interpreter.eval(NumericLiteral.of(10));
        Assertions.assertEquals(10, evalRes.getRuntimeValue());
    }

    @Test
    void stringLiteral() {
        var evalRes = interpreter.eval("\"hello world!\"");
        Assertions.assertEquals("\"hello world!\"", ((StringValue) evalRes).getValue());
    }

    @Test
    void stringLiterals() {
        var evalRes = interpreter.eval("hello world!");
        Assertions.assertEquals("hello world!", ((StringValue) evalRes).getValue());
    }

    @Test
    void boolFalse() {
        var evalRes = interpreter.eval(false);
        Assertions.assertFalse(((BooleanValue) evalRes).isValue());
    }

    @Test
    void boolTrue() {
        var evalRes = interpreter.eval(true);
        Assertions.assertTrue(((BooleanValue) evalRes).isValue());
    }

    @Test
    void NullTest() {
        var evalRes = interpreter.eval(new Identifier());
        Assertions.assertNull(((NullValue) evalRes).getValue());
    }
}
