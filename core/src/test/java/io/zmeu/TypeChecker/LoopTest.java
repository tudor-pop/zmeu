package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TypeChecker Loops")
public class LoopTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(src("""
                var x = 10
                while (x!=0) {
                   x--
                }
                x
                """));
        assertEquals(ValueType.Number, actual);
    }


    @Test
    void increment() {
        var res = checker.eval(src("""
                 var a = 0
                 var temp Number
                 for (var b = 1; a < 100; b = temp + b) {
                   temp = a;
                   a = b;
                 }
                """));
        assertEquals(ValueType.Number, res);
    }

    @Test
    void incrementEq() {
        var res = checker.eval(src("""
                 for (var a = 1; a < 10; a=a+1) {
                   a;
                 }
                """));
        assertEquals(ValueType.Number, res);
    }

    @Test
    void incrementTestOnly() {
        var res = checker.eval(src("""
                var a = 1
                 for (; a < 10; a=a+1) {
                   a
                 }
                """));
        assertEquals(ValueType.Number, res);
    }

    @Test
    void stringConcatenation() {
        var res = checker.eval(src("""
                var test = ""
                for (var a = 1; a < 10; a=a+1) {
                   test+=string(a);
                 }
                 test
                """));
        assertEquals(ValueType.String, res);
    }


}
