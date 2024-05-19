package io.zmeu.Frontend.TypeChecker;

import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Parse.BaseTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.*;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class TypeTest extends BaseTest {

    @Test
    void testString() {
        var res = parse("var x:String\n");
        var expected = program(var(id("x"), packageId("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringEOF() {
        var res = parse("var x:String");
        var expected = program(var(id("x"), packageId("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringLineTerminator() {
        var res = parse("var x:String;");
        var expected = program(var(id("x"), packageId("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringInit() {
        var res = parse("""
                var x:String="test"
                """);
        var expected = program(var(id("x"), packageId("String"), string("test")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringInitWrongTypeInt() {
        parse("""
                var x:String=1
                """);
        var errors = ErrorSystem.getErrors();
        log.info(ErrorSystem.errors());
        Assertions.assertFalse(errors.isEmpty());

    }

    @Test
    void testStringInitWrongTypeDecimal() {
        parse("""
                var x:String=1
                """);
        var errors = ErrorSystem.getErrors();
        log.info(ErrorSystem.errors());
        Assertions.assertFalse(errors.isEmpty());

    }

    @Test
    void testStringInitWrongType() {
        var actual = parse("""
                var x:Number="test"
                """);
        var errors = ErrorSystem.getErrors();
        log.info(actual);
        log.info(ErrorSystem.errors());
        Assertions.assertFalse(errors.isEmpty());

    }

    @Test
    void testNumberInitDouble() {
        var actual = parse("""
                var x:Number=0.2
                """);
        var errors = ErrorSystem.getErrors();
        log.info(actual);
        log.info(ErrorSystem.errors());
        Assertions.assertTrue(errors.isEmpty());

    }

    @Test
    void testNumberFromStd() {
        var actual = parse("""
                var x:std.Number
                """);
        var expected = program(var(id("x"), packageId("std.Number")));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }

    @Test
    void testNumberFromStdInit() {
        var actual = parse("""
                var x:std.Number=2
                """);
        var expected = program(var(id("x"), packageId("std.Number"), number(2)));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }
    @Test
    void testSpace() {
        var actual = parse("""
                var x    : std.Number=2
                """);
        var expected = program(var(id("x"), packageId("std.Number"), number(2)));
        assertEquals(expected, actual);
        log.info(toJson(actual));
    }


}
