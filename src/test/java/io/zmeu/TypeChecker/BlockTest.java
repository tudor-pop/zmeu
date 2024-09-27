package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parser.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TypeChecker Block")
public class BlockTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(src("""
                var x = 10
                var y = 10
                x*2+y
                """));
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testNestedBlock() {
        var actual = checker.eval(src("""
                var x = 10
                {
                    var x = "hello"
                }
                x * 2
                """));
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testScopeChainLookup() {
        var actual = checker.eval(src("""
                var x = 10
                {
                    var y = 10
                    x+y
                }
                """));
        assertEquals(ValueType.Number, actual);
    }
}
