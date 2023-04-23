package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Expressions.UnaryExpression;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class UnaryTest extends BaseTest {

    @Test
    void testLogicalUnary() {
        var res = parser.produceAST(tokenizer.tokenize("-x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("-", Identifier.of("x"))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalNot() {
        var res = parser.produceAST(tokenizer.tokenize("!x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("!", Identifier.of("x"))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void prefixDecrement() {
        var res = parser.produceAST(tokenizer.tokenize("--x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("--", Identifier.of("x")))
        );
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void postfixDecrement() {
        var res = parser.produceAST(tokenizer.tokenize("x--"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("--", Identifier.of("x")))
        );
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void prefixIncrement() {
        var res = parser.produceAST(tokenizer.tokenize("++x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("++", Identifier.of("x"))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void postfixIncrement() {
        var res = parser.produceAST(tokenizer.tokenize("x++"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("++", Identifier.of("x"))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalUnaryHigherPrecedenceThanMultiplication() {
        var res = parser.produceAST(tokenizer.tokenize("-x * 2"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of("*", UnaryExpression.of("-", Identifier.of("x")), Literal.of(2))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }


}
