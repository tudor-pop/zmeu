package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NullLiteral;
import io.zmeu.types.Types;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class BooleanTest extends BaseChecker {

    @Test
    void testTrue() {
        var t1 = checker.eval(true);
        assertEquals(t1, Types.Boolean);
    }

    @Test
    void testFalse() {
        var t1 = checker.eval(false);
        assertEquals(t1, Types.Boolean);
    }

    @Test
    void testTrueLiteral() {
        var t1 = checker.eval(bool(true));
        assertEquals(t1, Types.Boolean);
    }

    @Test
    void testFalseLiteral() {
        var t1 = checker.eval(bool(false));
        assertEquals(t1, Types.Boolean);
    }

    @Test
    void testNull() {
        var t1 = checker.eval(NullLiteral.of());
        assertEquals(t1, Types.Null);
    }

    @Test
    void testStringWithStringEq() {
        var type = checker.eval(src("""
                "hello" == "world"
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testStringWithStringNotEq() {
        var type = checker.eval(src("""
                "hello" != "world"
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testStringWithStringLess() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" < "world"
                """)));
    }

    @Test
    void testStringWithStringLessEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" <= "world"
                """)));
    }

    @Test
    void testStringWithStringGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" >= "world"
                """)));
    }

    @Test
    void testStringWithStringGreater() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "hello" > "world"
                """)));
    }

    @Test
    void testTrueWithTrueEq() {
        var type = checker.eval(src("""
                true == true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testTrueWithFalseEq() {
        var type = checker.eval(src("""
                true == false
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithFalseEq() {
        var type = checker.eval(src("""
                false == false
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithTrueEq() {
        var type = checker.eval(src("""
                false == true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithTrueLess() {
        var type = checker.eval(src("""
                false < true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithTrueLessEq() {
        var type = checker.eval(src("""
                false <= true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithTrueGreater() {
        var type = checker.eval(src("""
                false > true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testFalseWithTrueGreaterEq() {
        var type = checker.eval(src("""
                false >= true
                """));
        assertEquals(type, Types.Boolean);
    }

    @Test
    void testNumberWithTrueGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 >= true
                """)));
    }

    @Test
    void testNumberWithFalseGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 >= true
                """)));
    }

    @Test
    void testNumberWithBoolGreaterEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 > true
                """)));
    }

    @Test
    void testNumberWithBoolLessEq() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 <= true
                """)));
    }

    @Test
    void testNumberWithBoolLess() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                1 < true
                """)));
    }

    @Test
    void testNumberWithNumberLess() {
        var actual = checker.eval(src("""
                1 < 2
                """));
        assertEquals(actual, Types.Boolean);
    }

    @Test
    void testNumberWithNumberLessEq() {
        var actual = checker.eval(src("""
                1 <= 2
                """));
        assertEquals(actual, Types.Boolean);
    }

    @Test
    void testNumberWithNumberGreaterEq() {
        var actual = checker.eval(src("""
                1 >= 2
                """));
        assertEquals(actual, Types.Boolean);
    }

    @Test
    void testNumberWithNumberGreater() {
        var actual = checker.eval(src("""
                1 > 2
                """));
        assertEquals(actual, Types.Boolean);
    }

    @Test
    void testNumberWithNumberEq() {
        var actual = checker.eval(src("""
                1 == 2
                """));
        assertEquals(actual, Types.Boolean);
    }

    @Test
    void testNumberWithNumberNotEq() {
        var actual = checker.eval(src("""
                1 != 2
                """));
        assertEquals(actual, Types.Boolean);
    }

}