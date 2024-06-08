package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.TypeChecker.Types.DataTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhileTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(src("""
                var x = 10
                while (x!=0) {
                   x--
                }
                x
                """));
        assertEquals(DataTypes.Number, actual);
    }

}