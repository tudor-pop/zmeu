package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NullLiteral;
import io.zmeu.Frontend.TypeChecker.Types.DataTypes;
import io.zmeu.Runtime.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static io.zmeu.Frontend.Parser.Literals.Identifier.id;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.PathIdentifier.type;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VariableTest extends BaseChecker {

    @Test
    void testGlobalVarEmptyString() {
        checker = new TypeChecker(new TypeEnvironment());
        checker.getEnv().init("VERSION", DataTypes.String);
        var type = checker.eval(id("VERSION"));
        assertEquals(type, DataTypes.String);
    }

    @Test
    void testVarInt() {
        var type = checker.eval(var("x", number(10)));
        var accessType = checker.eval(id("x"));
        assertEquals(type, DataTypes.Number);
        assertEquals(accessType, DataTypes.Number);
    }

    @Test
    void testVarString() {
        var type = checker.eval(var("x", string("hello")));
        var accessType = checker.eval(id("x"));
        assertEquals(type, DataTypes.String);
        assertEquals(accessType, DataTypes.String);
    }

    @Test
    void testVarExplicitType() {
        var type = checker.eval(var("x", type("String"), string("hello")));
        var accessType = checker.eval(id("x"));
        assertEquals(type, DataTypes.String);
        assertEquals(accessType, DataTypes.String);
    }

    @Test
    void testVarExplicitTypeWrongNumberAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("String"), number(10))));
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("String"), number(10.1))));
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("String"), bool(true))));
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("String"), bool(false))));
    }

    @Test
    void testVarExplicitTypeWrongStringAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("Number"), string("10"))));
    }

    @Test
    void testExplicitTypeWrongBoolAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("Number"), bool(false))));
        assertThrows(TypeError.class, () -> checker.eval(var("x", type("Number"), bool(true))));
    }

    @Test
    void testGlobalVarNonExisting() {
        checker = new TypeChecker(new TypeEnvironment());
        assertThrows(NotFoundException.class, () -> checker.eval(id("VERSION")));
    }

    @Test
    void testNull() {
        var t = checker.eval(var("x", type("String"), NullLiteral.of()));
        assertEquals(t, DataTypes.String);
    }

    @Test
    void testInferTypeFromAnotherVar() {
        var t1 = checker.eval(var("x", type("String"), string("first")));
        var t2 = checker.eval(var("y", type("String"), id("x")));
        assertEquals(t1, DataTypes.String);
        assertEquals(t2, DataTypes.String);
    }
}
