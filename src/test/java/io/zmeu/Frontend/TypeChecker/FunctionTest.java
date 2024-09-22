package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Types.Type;
import io.zmeu.Frontend.Parser.Types.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeChecker Function")
public class FunctionTest extends BaseChecker {

    @Test
    void testFunInputAndReturn() {
        var actual = checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                // square(2)
                """));
        assertNotNull(actual);
        Type expected = Type.fromString("(Number)->Number");
        assertEquals(expected, actual);
    }

    @Test
    void testNoParamTypes() {
        assertThrows(IllegalArgumentException.class, () -> {
            checker.eval(src("""
                    fun square(x) :Number { return x * x }
                    """));
        });
    }

    @Test
    void testNoParams() {
        var actual = checker.eval(src("""
                fun square() :Number { return 2 * 2 }
                """));
        Type expected = Type.fromString("()->Number");
        assertEquals(expected, actual);
    }

    @Test
    void testVoidThrows() {
        assertThrows(TypeError.class, () -> {
            checker.eval(src("""
                    fun square() :Void { return 2 * 2 }
                    """));
        });
    }

    @Test
    void testFunCall() {
        var actual = checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square(2)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testFunCallDecimal() {
        var actual = checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square(2.2)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testFunCallWrongArg() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square("2")
                """
        )));
    }

    @Test
    void testFunCallWrongNumberOfArg() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                fun square(x :Number) :Number {
                    return x * x
                }
                square(2,3)
                """
        )));
    }

    @Test
    void testFunCallWrongNumberOfParam() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                fun square(x :Number,y :Number) :Number {
                    return x * y
                }
                square(2)
                """
        )));
    }

    @Test
    void testMultiInputsAndReturn() {
        var actual = checker.eval(src("""
                fun multiply(x :Number,y :Number) :Number {
                    return x * y
                }
                multiply(2, 3)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testBuiltIn() {
        var actual = checker.eval(src("""
                pow(2, 3)
                """));
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testWrongArgType() {
        Assertions.assertThrows(TypeError.class, () -> checker.eval(src("""
                fun multiply(x :Number,y :Number) :Number {
                    return x * y
                }
                multiply(2, "3")
                """
        )));
    }

    @Test
    void testHigherOrderFunction() {
        var actual = checker.eval(src("""
                var global=10
                fun outer(x :Number, y :Number) :(Number)->Number {
                    var z = x+y
                
                    fun inner(p:Number):Number {
                        return p+z+global
                    }
                    return inner
                }
                var fn = outer(1,1)
                fn(1)
                """
        ));

        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testHigherOrderFunctionMultipleArgs() {
        var actual = checker.eval(src("""
                var global=10
                fun outer(x :Number, y :Number) :(Number,Number,Number)->Number {
                    var z = x+y
                
                    fun inner(p:Number,q:Number,o:Number):Number {
                        return p+z+global
                    }
                    return inner
                }
                var fn = outer(1,1)
                fn(1,2,3)
                """
        ));

        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

}
