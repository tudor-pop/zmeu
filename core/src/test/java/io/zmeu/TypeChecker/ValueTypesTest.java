package io.zmeu.TypeChecker;

import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Parse.BaseTest;
import io.zmeu.Frontend.Parser.Factory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VarDeclaration.var;
import static io.zmeu.Frontend.Parser.Factory.number;
import static io.zmeu.Frontend.Parser.Factory.program;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.id;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.type;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("TypeChecker Type")
public class ValueTypesTest extends BaseTest {

    @Test
    void testString() {
        var res = parse("var x String\n");
        var expected = program(Factory.var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringEOF() {
        var res = parse("var x String");
        var expected = program(Factory.var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringLineTerminator() {
        var res = parse("var x String;");
        var expected = program(Factory.var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringInit() {
        var res = parse("""
                var x String="test"
                """);
        var expected = program(var(id("x"), type("String"), string("test")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testNumberInitDouble() {
        var actual = parse("""
                var x Number=0.2
                """);
        var errors = ErrorSystem.getErrors();
        log.info(actual);
        log.info(ErrorSystem.errors());
        Assertions.assertTrue(errors.isEmpty());

    }

    @Test
    void testNumberFromStd() {
        var actual = parse("""
                var x std.Number
                """);
        var expected = program(Factory.var(id("x"), type("std.Number")));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }

    @Test
    void testNumberFromStdInit() {
        var actual = parse("""
                var x std.Number=2
                """);
        var expected = program(var(id("x"), type("std.Number"), number(2)));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }
    @Test
    void testSpace() {
        var actual = parse("""
                var x      std.Number=2
                """);
        var expected = program(var(id("x"), type("std.Number"), number(2)));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }


}
