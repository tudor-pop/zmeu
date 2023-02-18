package dev.fangscl.Runtime;

import dev.fangscl.Runtime.TypeSystem.Literals.Identifier;
import dev.fangscl.Runtime.TypeSystem.Literals.IntegerLiteral;
import dev.fangscl.Runtime.Values.BooleanValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.NullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterpreterTest {
    protected Interpreter interpreter;

    @BeforeEach
    void reset() {
        this.interpreter = new Interpreter();
    }

    @Test
    void testLiteral() {
        var evalRes = interpreter.eval(new IntegerLiteral(10));
        Assertions.assertEquals(10, ((IntegerValue) evalRes).getValue());
    }

    @Test
    void testBoolFalse() {
        var evalRes = interpreter.eval(new Identifier(false));
        Assertions.assertFalse(((BooleanValue) evalRes).isValue());
    }
    @Test
    void testBoolTrue() {
        var evalRes = interpreter.eval(new Identifier(true));
        Assertions.assertTrue(((BooleanValue) evalRes).isValue());
    }
    @Test
    void testNull() {
        var evalRes = interpreter.eval(new Identifier());
        Assertions.assertNull(((NullValue) evalRes).getValue());
    }


}