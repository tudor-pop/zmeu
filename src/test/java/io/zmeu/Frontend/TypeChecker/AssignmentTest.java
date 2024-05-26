package io.zmeu.Frontend.TypeChecker;

import io.zmeu.types.Types;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssignmentTest extends BaseChecker {

    @Test
    void testSimpleAssignment() {
        var actual = checker.eval(src("""
                var x = 10
                x = 1
                """));
        assertEquals(Types.Number, actual);
    }

    @Test
    void testWrongAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(src("""
                var x = 10 // number
                x = false // assign boolean should throw exception
                """)));
    }


}
