package dev.fangscl.Runtime;

import com.google.gson.Gson;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.Values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseTest {
    protected Interpreter interpreter;
    protected Gson gson = new Gson();

    @BeforeEach
    void reset() {
        this.interpreter = new Interpreter();
    }

    @Test
    void testLiteral() {
        var evalRes = interpreter.eval(10);
        Assertions.assertEquals(10, ((IntegerValue) evalRes).getValue());
    }

    @Test
    void testLiteralDouble() {
        var evalRes = interpreter.eval(10.1);
        Assertions.assertEquals(10.1, ((DecimalValue) evalRes).getValue());
    }

    @Test
    void testIntegerLiteral() {
        var evalRes = interpreter.eval(new NumericLiteral(10));
        Assertions.assertEquals(10, ((IntegerValue) evalRes).getValue());
    }

    @Test
    void testStringLiteral() {
        var evalRes = interpreter.eval("\"hello world!\"");
        Assertions.assertEquals("\"hello world!\"", ((StringValue) evalRes).getValue());
    }

    @Test
    void testStringLiterals() {
        var evalRes = interpreter.eval("hello world!");
        Assertions.assertEquals("hello world!", ((StringValue) evalRes).getValue());
    }

    @Test
    void testBoolFalse() {
        var evalRes = interpreter.eval(false);
        Assertions.assertFalse(((BooleanValue) evalRes).isValue());
    }

    @Test
    void testBoolTrue() {
        var evalRes = interpreter.eval(true);
        Assertions.assertTrue(((BooleanValue) evalRes).isValue());
    }

    @Test
    void testNull() {
        var evalRes = interpreter.eval(new Identifier());
        Assertions.assertNull(((NullValue) evalRes).getValue());
    }

//    @Test
//    void testList() {
//        var evalRes = interpreter.eval(Arrays.asList("+", 1, 2, Arrays.asList("*", 2, 3)), new Environment());
//        Assertions.assertEquals(3, ((IntegerValue) evalRes).getValue());
//    }
}