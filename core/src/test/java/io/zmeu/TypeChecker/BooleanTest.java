package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NullLiteral;
import io.zmeu.TypeChecker.Types.ValueType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("TypeChecker Boolean")
class BooleanTest extends BaseChecker {

    @Test
    void testTrue() {
        var t1 = checker.eval(true);
        assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testFalse() {
        var t1 = checker.eval(false);
        assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testTrueLiteral() {
        var t1 = checker.eval(bool(true));
        assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testFalseLiteral() {
        var t1 = checker.eval(bool(false));
        assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testNull() {
        var t1 = checker.eval(NullLiteral.of());
        assertEquals(t1, ValueType.Null);
    }

    @Test
    void testEq() {
        var t1 = eval("1==1");
        Assertions.assertEquals(t1, ValueType.Boolean);
    }

    @Test
    void testStringWithStringEq() {
        var type = eval("""
                "hello" == "world"
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testStringWithStringNotEq() {
        var type = eval("""
                "hello" != "world"
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testStringWithStringLess() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" < "world"
                """));
    }

    @Test
    void testStringWithStringLessEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" <= "world"
                """));
    }

    @Test
    void testStringWithStringGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" >= "world"
                """));
    }

    @Test
    void testStringWithStringGreater() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" > "world"
                """));
    }

    @Test
    void testTrueWithTrueEq() {
        var type = eval("""
                true == true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testTrueWithFalseEq() {
        var type = eval("""
                true == false
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithFalseEq() {
        var type = eval("""
                false == false
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithTrueEq() {
        var type = eval("""
                false == true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithTrueLess() {
        var type = eval("""
                false < true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithTrueLessEq() {
        var type = eval("""
                false <= true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithTrueGreater() {
        var type = eval("""
                false > true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testFalseWithTrueGreaterEq() {
        var type = eval("""
                false >= true
                """);
        assertEquals(type, ValueType.Boolean);
    }

    @Test
    void testNumberWithTrueGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                1 >= true
                """));
    }

    @Test
    void testNumberWithFalseGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                1 >= true
                """));
    }

    @Test
    void testNumberWithBoolGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                1 > true
                """));
    }

    @Test
    void testNumberWithBoolLessEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                1 <= true
                """));
    }

    @Test
    void testNumberWithBoolLess() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                1 < true
                """));
    }

    @Test
    void testNumberWithNumberLess() {
        var actual = eval("""
                1 < 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testNumberWithNumberLessEq() {
        var actual = eval("""
                1 <= 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testNumberWithNumberGreaterEq() {
        var actual = eval("""
                1 >= 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testNumberWithNumberGreater() {
        var actual = eval("""
                1 > 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testNumberWithNumberEq() {
        var actual = eval("""
                1 == 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testNumberWithNumberNotEq() {
        var actual = eval("""
                1 != 2
                """);
        assertEquals(actual, ValueType.Boolean);
    }

    @Test
    void testStringWithNumberLess() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" < 1
                """));
    }

    @Test
    void testStringWithNumberLessEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" <= 1
                """));
    }

    @Test
    void testStringWithNumberGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" >= 1
                """));
    }

    @Test
    void testStringWithNumberEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" == 1
                """));
    }

    @Test
    void testStringWithNumberNotEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                "hello" != 1
                """));
    }

    @Test
    void testBoolWithNumberNotEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true != 1
                """));
    }

    @Test
    void testBoolWithNumberEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true == 1
                """));
    }

    @Test
    void testBoolWithNumberGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true >= 1
                """));
    }

    @Test
    void testBoolWithNumberGreater() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true > 1
                """));
    }

    @Test
    void testBoolWithNumberLess() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true < 1
                """));
    }

    @Test
    void testBoolWithNumberLessEq() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                true <= 1
                """));
    }


}