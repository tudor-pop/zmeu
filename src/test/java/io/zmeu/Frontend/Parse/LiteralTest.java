package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LiteralTest extends BaseTest {

    @Test
    void testInteger() {
        var res = parse("1");
        var expected = Program.of(ExpressionStatement.of(NumericLiteral.of(1)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDecimal() {
        var res = parse("1.11");
        var expected = Program.of(ExpressionStatement.of(NumericLiteral.of(1.11)));
        assertEquals(expected, res);
    }

    @Test
    void testStringStatements() {
        var res = parse("""
                "Hello"
                """);
        var expected = Program.of(
                ExpressionStatement.of("Hello")
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMultipleStatements() {
        var res = parse("""
                "Hello"
                1
                """);
        var expected = Program.of(
                ExpressionStatement.of("Hello"),
                ExpressionStatement.of(1)
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }


    @Test
    void testIntegerStrShouldEvalToString() {
        var res = parse(""" 
                "42" 
                """);
        var expected = Program.of(
                ExpressionStatement.of("42")
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testSingleQuotesShouldEvalToString() {
        var res = parse(""" 
                '42' 
                """);
        var expected = Program.of(
                ExpressionStatement.of("42")
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testSingleQuotesWithSpaceShouldEvalToString() {
        var res = parse(""" 
                '  42  ' 
                """);
        var expected = Program.of(
                ExpressionStatement.of("  42  ")
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testNumberStringShouldEvalToNumber() {
        var res = parse("42");
        var expected = Program.of(
                ExpressionStatement.of(42)
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testNumberStringShouldEvalToNumberWithTrailingSpace() {
        var res = parse("   \"  42  \"    ");
        var expected = Program.of(
                ExpressionStatement.of("  42  ")
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

}