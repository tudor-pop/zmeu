package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.Type;
import io.zmeu.TypeChecker.Types.TypeFactory;
import io.zmeu.TypeChecker.Types.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeChecker Function")
public class FunctionTest extends BaseChecker {

    @Test
    void testFunInputAndReturn() {
        var actual = eval("""
                fun square(x  Number)  Number {
                    return x * x
                }
                // square(2)
                """);
        assertNotNull(actual);
        Type expected = TypeFactory.fromString("(Number)->Number");
        assertEquals(expected, actual);
    }

    @Test
    void testNoParamTypes() {
        assertThrows(IllegalArgumentException.class, () -> eval(" fun square(x)  Number { return x * x } "));
    }

    @Test
    void testNoParams() {
        var actual = eval(" fun square()  Number { return 2 * 2 } ");
        Type expected = TypeFactory.fromString("()->Number");
        assertEquals(expected, actual);
    }

    @Test
    void testVoidThrows() {
        assertThrows(TypeError.class, () -> eval(" fun square()  Void { return 2 * 2 } "));
    }

    @Test
    void testVoidDoesntThrow() {
        var actual = eval(" fun square()  Void { return; } ");
        Type expected = TypeFactory.fromString("()->Void");
        assertEquals(expected, actual);
    }

    @Test
    void testVoidNoReturnDefauly() {
        var actual = eval(" fun square() { return; } ");
        Type expected = TypeFactory.fromString("()->Void");
        assertEquals(expected, actual);
    }

    @Test
    void testThrowIfVoidButReturns() {
        assertThrows(TypeError.class, () -> eval(" fun square() { return 2; } "));
    }


    @Test
    void testFunCall() {
        var actual = eval("""
                fun square(x  Number)  Number {
                    return x * x
                }
                square(2)
                """);
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testFunCallDecimal() {
        var actual = eval("""
                fun square(x  Number)  Number {
                    return x * x
                }
                square(2.2)
                """);
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testFunCallWrongArg() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                fun square(x  Number)  Number {
                    return x * x
                }
                square("2")
                """));
    }

    @Test
    void testFunCallWrongNumberOfArg() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                fun square(x  Number)  Number {
                    return x * x
                }
                square(2,3)
                """));
    }

    @Test
    void testFunCallWrongNumberOfParam() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                fun square(x  Number,y  Number)  Number {
                    return x * y
                }
                square(2)
                """));
    }

    @Test
    void testMultiInputsAndReturn() {
        var actual = eval("""
                fun multiply(x  Number,y  Number)  Number {
                    return x * y
                }
                multiply(2, 3)
                """);
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testBuiltIn() {
        var actual = eval(" pow(2, 3) ");
        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testWrongArgType() {
        Assertions.assertThrows(TypeError.class, () -> eval("""
                fun multiply(x  Number,y  Number)  Number {
                    return x * y
                }
                multiply(2, "3")
                """));
    }

    @Test
    void testHigherOrderFunction() {
        var actual = eval("""
                var global=10
                fun outer(x  Number, y  Number)  (Number)->Number {
                    var z = x+y
                
                    fun inner(p Number) Number {
                        return p+z+global
                    }
                    return inner
                }
                var fn = outer(1,1)
                fn(1)
                """);

        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testClojureFunctionMultipleArgs() {
        var actual = eval("""
                var global=10
                fun outer(x  Number, y  Number)  (Number,Number,Number)->Number {
                    var z = x+y
                
                    fun inner(p Number,q Number,o Number) Number {
                        return p+z+global
                    }
                    return inner
                }
                var fn = outer(1,1)
                fn(1,2,3)
                """);

        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testRecursionFun() {
        var actual = eval("""
                fun recursive(x  Number)  Number {
                    if (x == 1) return x
                    return recursive(x-1)
                }
                recursive(3)
                """);

        assertNotNull(actual);
        assertEquals(ValueType.Number, actual);
    }

}
