package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NullLiteral;
import io.zmeu.Frontend.Parser.Types.ValueType;
import io.zmeu.TypeChecker.TypeError;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;

@Log4j2
@DisplayName("TypeChecker Literal")
class LiteralTest extends BaseChecker {

    @Test
    void testInteger() {
        var t1 = checker.eval(number(1));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testIntegerLiteral() {
        var t1 = checker.eval(1);
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testFloatLiteral() {
        var t1 = checker.eval(1.1);
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testDoubleLiteral() {
        var t1 = checker.eval(1.1);
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testBooleanTrueLiteral() {
        var t1 = checker.eval(true);
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testBooleanFalseLiteral() {
        var t1 = checker.eval(false);
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testBoolTrue() {
        var t1 = checker.eval(bool(true));
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testNull() {
        var t1 = checker.eval(NullLiteral.of());
        Assertions.assertEquals(t1, ValueType.Null);
    }

    @Test
    void testBoolFalseLiteral() {
        var t1 = checker.eval(true);
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testStringLiteral() {
        var t1 = checker.eval("hello");
        Assertions.assertEquals(t1, ValueType.String);
    }

    @Test
    void testDecimal() {
        var t1 = checker.eval(number(1.1));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testBoolean() {
        var t1 = checker.eval(bool(true));
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testAddition() {
        var t1 = checker.eval(src("1+1"));
        Assertions.assertEquals(t1, ValueType.Number);
    }


    @Test
    void testSubstraction() {
        var t1 = checker.eval(src("1-1"));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testDivision() {
        var t1 = checker.eval(src("1/1"));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testMod() {
        var t1 = checker.eval(src("1 % 1"));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testMultiplication() {
        var t1 = checker.eval(src("1*1"));
        Assertions.assertEquals(t1, ValueType.Number);
    }

    @Test
    void testNumberWithStringAddition() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "1"+1
                """)));
    }

    @Test
    void testStringWithNumberAddition() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1+"1"
                """)));
    }

    @Test
    void testStringWithNumberEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 == "1"
                """)));
    }

    @Test
    void testStringWithNumberMod() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("%", number(1), string("1"))));
    }

    @Test
    void testStringWithStringAddition() {
        var actual = checker.eval(src("""
                "hello" + "world"
                """));
        Assertions.assertEquals(actual, ValueType.String);
    }

    @Test
    void testStringWithStringSubstraction() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" - "world"
                """)));
    }

    @Test
    void testStringWithStringDivision() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" / "world"
                 """)));
    }

    @Test
    void testStringWithStringMultiplication() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("*", string("hello"), string("world"))));
    }

    @Test
    void testStringWithStringMod() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(binary("%", string("hello"), string("world"))));
    }

}