package io.zmeu.TypeChecker;

import io.zmeu.Runtime.exceptions.NotFoundException;
import io.zmeu.TypeChecker.Types.ValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.ValDeclaration.val;
import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;
import static io.zmeu.Frontend.Parser.Literals.Identifier.id;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.type;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("TypeChecker Val")
public class ValTest extends BaseChecker {

    @Test
    void testGlobalVarEmptyString() {
        checker = new TypeChecker(new TypeEnvironment());
        checker.getEnv().init("VERSION", ValueType.String);
        var type = checker.visit(id("VERSION"));
        assertEquals(type, ValueType.String);
    }

    @Test
    void testVarInt() {
        var type = checker.visit(val("x", number(10)));
        var accessType = checker.visit(id("x"));
        assertEquals(type, ValueType.Number);
        assertEquals(accessType, ValueType.Number);
    }

    @Test
    void testVarString() {
        var type = checker.visit(val("x", string("hello")));
        var accessType = checker.visit(id("x"));
        assertEquals(type, ValueType.String);
        assertEquals(accessType, ValueType.String);
    }

    @Test
    void testVarExplicitType() {
        var type = checker.visit(val("x", type("String"), string("hello")));
        var accessType = checker.visit(id("x"));
        assertEquals(type, ValueType.String);
        assertEquals(accessType, ValueType.String);
    }

    @Test
    void testVarExplicitTypeWrongNumberAssignment() {
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("String"), number(10))));
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("String"), number(10.1))));
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("String"), bool(true))));
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("String"), bool(false))));
    }

    @Test
    void testVarExplicitTypeWrongStringAssignment() {
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("Number"), string("10"))));
    }

    @Test
    void testExplicitTypeWrongBoolAssignment() {
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("Number"), bool(false))));
        assertThrows(TypeError.class, () -> checker.visit(val("x", type("Number"), bool(true))));
    }

    @Test
    void testGlobalVarNonExisting() {
        checker = new TypeChecker(new TypeEnvironment());
        assertThrows(NotFoundException.class, () -> checker.visit(id("VERSION")));
    }

    @Test
    void testNull() {
        var t = checker.visit(val("x", type("String"), string("")));
        assertEquals(t, ValueType.String);
    }

    @Test
    void testInferTypeFromAnotherVar() {
        var t1 = checker.visit(val("x", type("String"), string("first")));
        var t2 = checker.visit(val("y", type("String"), id("x")));
        assertEquals(t1, ValueType.String);
        assertEquals(t2, ValueType.String);
    }
}
