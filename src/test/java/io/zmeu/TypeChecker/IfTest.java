package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parser.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TypeChecker If")
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
        assertEquals(ValueType.Number, actual);
    }

}
