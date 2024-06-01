package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.TypeChecker.Types.DataTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest extends BaseChecker {

    @Test
    void testFunInputAndReturn() {
        var actual = checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square(2)
                """));
        assertEquals(DataTypes.valueOf("fun(Number):Number"), actual);
    }

    @Test
    void testMultiInputsAndReturn() {
        var actual = checker.eval(src("""
                fun multiply(x :Number,y :Number) :Number {
                    return x * y
                }
                multiply(2,3)
                """));
        assertEquals(DataTypes.Number, actual);
    }

}
