package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parser.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TypeChecker While")
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
        assertEquals(ValueType.Number, actual);
    }

}
