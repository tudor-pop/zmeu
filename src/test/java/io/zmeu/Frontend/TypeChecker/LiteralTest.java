package io.zmeu.Frontend.TypeChecker;

import io.zmeu.types.Types;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;

@Log4j2
class LiteralTest {
    private TypeChecker typeChecker;

    @BeforeEach
    void setUp() {
        typeChecker = new TypeChecker();
    }

    @Test
    void testInteger() {
        var t1 = typeChecker.eval(number(1));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testDecimal() {
        var t1 = typeChecker.eval(number(1.1));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testBoolean() {
        var t1 = typeChecker.eval(bool(true));
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testAddition() {
        var t1 = typeChecker.eval(binary(1, 1, "+"));
        Assertions.assertEquals(t1, Types.Number);
    }


    @Test
    void testSubstraction() {
        var t1 = typeChecker.eval(binary(1, 1, "-"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testDivision() {
        var t1 = typeChecker.eval(binary(1, 1, "-"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testMultiplication() {
        var t1 = typeChecker.eval(binary(1, 1, "-"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testNumberWithStringError() {
        Assertions.assertThrows(TypeError.class, () -> typeChecker.eval(binary("+", string("1"), number(1))));
    }

    @Test
    void testStringWithNumberError() {
        Assertions.assertThrows(TypeError.class, () -> typeChecker.eval(binary("+", number(1), string("1"))));
    }

}