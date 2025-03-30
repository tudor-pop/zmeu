package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TypeChecker Lambdas")
public class LambdaTest extends BaseChecker {

    @Test
    void lambdaWithReturn() {
        var actual = checker.eval(src("""
                fun onClick(callback (Number)->Number) Number {
                    return callback(2)
                }
                onClick((arg Number) Number -> arg*2)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void lambdaWithReturnInferred() {
        var actual = checker.eval(src("""
                fun onClick(callback  (Number)->Number) Number {
                    return callback(2)
                }
                onClick((arg Number) -> arg*2)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void lambdaImmediatelyInvoke() {
        var actual = checker.eval(src("""
                ((x Number) Number -> x+2)(3)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void varLambdaInvokeByVariable() {
        var actual = checker.eval(src("""
                var lambda = (x Number) Number -> x+2
                lambda(3)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }


}
