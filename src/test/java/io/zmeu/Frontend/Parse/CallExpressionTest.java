package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.CallExpression;
import io.zmeu.Frontend.Parser.Expressions.MemberExpression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class CallExpressionTest extends BaseTest {

    @Test
    void testFunctionCall() {
        var res = parse("foo(x)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call("foo", "x")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testFunctionCallNumber() {
        var res = parse("foo(2)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call("foo", 2)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testFunctionCallDecimal() {
        var res = parse("foo(2.2)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call("foo", 2.2)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleArgs() {
        var res = parse("foo(x,y)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call("foo", "x","y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleCalls() {
        var res = parse("foo(x)()");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call(CallExpression.call("foo", "x"), Collections.emptyList())));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleCallsArgs() {
        var res = parse("foo(x)(y)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call(CallExpression.call("foo", "x"), "y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignment() {
        var res = parse("console.log(x,y)");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                CallExpression.call(
                        MemberExpression.of(false,"console", "log"),
                        Identifier.of("x", "y"))
        ));

        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
