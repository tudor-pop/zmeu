package io.zmeu.Frontend.TypeChecker;

import io.zmeu.types.Types;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariableTest extends BaseChecker{

    @Test
    void testVarInt() {
        var type = checker.eval(var("x", number(10)));
        assertEquals(type, Types.Number);
    }
    @Test
    void testGlobalVarEmptyString() {
        var type = checker.eval(var("VERSION"));
        assertEquals(type, Types.String);
    }
}
