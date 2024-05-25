package io.zmeu.Frontend.TypeChecker;

import io.zmeu.types.Types;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;

@Log4j2
class LiteralTest extends BaseChecker{

    @Test
    void testInteger() {
        var t1 = checker.eval(number(1));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testDecimal() {
        var t1 = checker.eval(number(1.1));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testBoolean() {
        var t1 = checker.eval(bool(true));
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testAddition() {
        var t1 = checker.eval(binary(1, 1, "+"));
        Assertions.assertEquals(t1, Types.Number);
    }


    @Test
    void testSubstraction() {
        var t1 = checker.eval(binary(1, 1, "-"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testDivision() {
        var t1 = checker.eval(binary(1, 1, "/"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testMod() {
        var t1 = checker.eval(binary(1, 1, "%"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testEq() {
        var t1 = checker.eval(binary(1, 1, "=="));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testMultiplication() {
        var t1 = checker.eval(binary(1, 1, "*"));
        Assertions.assertEquals(t1, Types.Number);
    }

    @Test
    void testNumberWithStringAddition() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("+", string("1"), number(1))));
    }

    @Test
    void testStringWithNumberAddition() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("+", number(1), string("1"))));
    }

    @Test
    void testStringWithNumberEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("==", number(1), string("1"))));
    }

    @Test
    void testStringWithNumberMod() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("%", number(1), string("1"))));
    }

    @Test
    void testStringWithStringAddition() {
        var actual = checker.eval(binary("+", string("hello"), string("world")));
        Assertions.assertEquals(actual, Types.String);
    }

    @Test
    void testStringWithStringSubstraction() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("-", string("hello"), string("world"))));
    }

    @Test
    void testStringWithStringDivision() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("/", string("hello"), string("world"))));
    }

    @Test
    void testStringWithStringMultiplication() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("*", string("hello"), string("world"))));
    }

    @Test
    void testStringWithStringMod() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("%", string("hello"), string("world"))));
    }

    @Test
    void testStringWithStringEq() {
        var type = checker.eval(binary("==", string("hello"), string("world")));
        Assertions.assertEquals(type, Types.String);
    }

}