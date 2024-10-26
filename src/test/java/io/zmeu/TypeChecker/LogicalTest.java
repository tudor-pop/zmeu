package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogicalTest extends BaseChecker {

    @Test
    void trueOrTrue() {
        var res = checker.eval(src("true || true"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void trueOrFalse() {
        // true || false -> true
        var res = checker.eval(src("true || false"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void falseOrTrue() {
        // false || true -> true
        var res = checker.eval(src("false || true"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void falseOrFalse() {
        // false || false -> true
        var res = checker.eval(src("false || false"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void nullAndNumber() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("null && 2")));
    }

    @Test
    void nullOrNumber() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("null || 2")));
    }

    @Test
    void booleanOrNumber() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("true || 2")));
    }

    @Test
    void booleanOrString() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                true || "test" 
                """)));
    }

    @Test
    void booleanAndNumber() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                true && 3 
                """)));
    }

    @Test
    void numberAndBoolean() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                3 && true 
                """)));
    }

    @Test
    void booleanAndString() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                true && "test" 
                """)));
    }

    @Test
    void stringAndBoolean() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "test" && true 
                """)));
    }


    @Test
    void stringAndFalse() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                "test" && false 
                """)));
    }

    @Test
    void nullAndFalse() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                null && false 
                """)));
    }

    @Test
    void nullOrFalse() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                null || false 
                """)));
    }

    @Test
    void nullAndTrue() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                null && true 
                """)));
    }

    @Test
    void nullOrTrue() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                null || true 
                """)));
    }

    @Test
    void numberAndNull() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("2 && null")));
    }

    @Test
    void numberAndNumber() {
        // 1 && 2 -> throw
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("1 && 2")));
    }

    @Test
    void falseAndFalse() {
        // false && false -> false
        var res = checker.eval(src("false && false"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void trueAndFalse() {
        // false && false -> false
        var res = checker.eval(src("true && false"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void trueAndTrue() {
        // false && false -> false
        var res = checker.eval(src("true && true"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

    @Test
    void falseAndTrue() {
        // false && false -> false
        var res = checker.eval(src("false && true"));
        Assertions.assertEquals(ValueType.Boolean, res);
    }

}
