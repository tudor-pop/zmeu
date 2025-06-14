package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parse.BaseTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VarDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.*;
import static io.zmeu.Frontend.Parser.Program.program;
import static io.zmeu.Frontend.Parser.Statements.VarStatement.statement;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("Parser Type")
public class TypeDeclarationTest extends BaseTest {

    @Test
    void testNumber() {
        var res = parse("var x Number");
        var expected = program(statement(var("x", type("Number"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testBoolean() {
        var res = parse("var x Boolean");
        var expected = program(statement(var("x", type("Boolean"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testString() {
        var res = parse("var x String");
        var expected = program(statement(var("x", type("String"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testCustom() {
        var res = parse("var x Subnet");
        var expected = program(statement(var("x", type("Subnet"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testCustomPath() {
        var res = parse("var x  Aws.Networking.Subnet");
        var expected = program(statement(var("x", type("Aws.Networking.Subnet"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
