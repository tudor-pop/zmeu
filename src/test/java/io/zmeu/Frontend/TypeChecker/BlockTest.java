package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.TypeChecker.Types.DataTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(src("""
                var x = 10
                var y = 10
                x*2+y
                """));
        assertEquals(DataTypes.Number, actual);
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
        assertEquals(DataTypes.Number, actual);
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
        assertEquals(DataTypes.Number, actual);
    }
}
