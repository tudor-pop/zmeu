package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.CallExpression;
import dev.fangscl.Frontend.Parser.Expressions.MemberExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class CallExpressionTest extends BaseTest {

    @Test
    void testFunctionCall() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of("foo", "x")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testFunctionCallNumber() {
        var res = parser.produceAST(tokenizer.tokenize("foo(2)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of("foo", 2)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testFunctionCallDecimal() {
        var res = parser.produceAST(tokenizer.tokenize("foo(2.2)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of("foo", 2.2)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleArgs() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x,y)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of("foo", "x","y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleCalls() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x)()"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of(CallExpression.of("foo", "x"), Collections.emptyList())));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void functionMultipleCallsArgs() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x)(y)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of(CallExpression.of("foo", "x"), "y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignment() {
        var res = parser.produceAST(tokenizer.tokenize("console.log(x,y)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of(
                        MemberExpression.of(false,"console", "log"),
                        Identifier.of("x", "y"))
        ));

        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
