package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LiteralTest {
    private TypeChecker typeChecker;

    @BeforeEach
    void setUp() {
        typeChecker = new TypeChecker();
    }

    @Test
    void testInteger() {
        var t1 = typeChecker.eval(NumericLiteral.of(1));
        Assertions.assertEquals(t1, TypeValue.INTEGER);
    }

    @Test
    void testDecimal() {
        var t1 = typeChecker.eval(NumericLiteral.of(1.1));
        Assertions.assertEquals(t1, TypeValue.DECIMAL);
    }
    @Test
    void testBoolean() {
        var t1 = typeChecker.eval(NumericLiteral.of(1.1));
        Assertions.assertEquals(t1, TypeValue.DECIMAL);
    }

}