package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Runtime.exceptions.NotFoundException;
import io.zmeu.types.Types;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.Identifier.id;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.PathIdentifier.type;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VariableTest extends BaseChecker {

    @Test
    void testVarInt() {
        var type = checker.eval(var("x", number(10)));
        var accessType = checker.eval(id("x"));
        assertEquals(type, Types.Number);
        assertEquals(accessType, Types.Number);
    }

    @Test
    void testVarString() {
        var type = checker.eval(var("x", string("hello")));
        var accessType = checker.eval(id("x"));
        assertEquals(type, Types.String);
        assertEquals(accessType, Types.String);
    }

    @Test
    void testVarExplicitType() {
        var type = checker.eval(var("x", type("String"), string("hello")));
        var accessType = checker.eval(id("x"));
        assertEquals(type, Types.String);
        assertEquals(accessType, Types.String);
    }

    @Test
    void testVarExplicitTypeWrongNumberAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("String"), number(10))));
    }
    @Test
    void testVarExplicitTypeWrongStringAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("Number"), string("10"))));
    }

    @Test
    void testGlobalVarEmptyString() {
        checker = new TypeChecker(new TypeEnvironment());
        checker.getEnvironment().init("VERSION", Types.String);
        var type = checker.eval(id("VERSION"));
        assertEquals(type, Types.String);
    }

    @Test
    void testGlobalVarNonExisting() {
        checker = new TypeChecker(new TypeEnvironment());
        assertThrows(NotFoundException.class, () -> checker.eval(id("VERSION")));
    }

}
