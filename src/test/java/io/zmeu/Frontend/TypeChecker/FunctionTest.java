package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Types.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TypeChecker Function")
public class FunctionTest extends BaseChecker {

    @Test
    void testFunInputAndReturn() {
        var actual = checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square(2)
                """));
        assertNotNull(actual);
        assertEquals(Type.valueOf("fun(Number):Number"), actual);
    }

    @Test
    void testMultiInputsAndReturn() {
        var actual = checker.eval(src("""
                fun multiply(x :Number,y :Number) :Number {
                    return x * y
                }
                multiply(2,3)
                """));
        assertNotNull(actual);
        assertEquals(Type.valueOf("fun(Number,Number):Number"), actual);
    }

}
