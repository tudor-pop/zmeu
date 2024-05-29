package io.zmeu.Frontend.TypeChecker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(src("""
                var x = 10
                var y = 10
                if (x<=10) {
                   y = 2
                } else {
                   y = 3
                }
                y
                """));
        assertEquals(DataTypes.Number, actual);
    }

}
