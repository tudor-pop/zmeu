package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.UnaryExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class UnaryTest extends StatementTest {

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
    void testLogicalUnaryDouble() {
        var res = parser.produceAST(tokenizer.tokenize("--x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("-", UnaryExpression.of("-", Identifier.of("x")))
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalUnaryDoublePlus() {
        var res = parser.produceAST(tokenizer.tokenize("++x"));
        var expected = Program.of(ExpressionStatement.of(
                UnaryExpression.of("+", UnaryExpression.of("+", Identifier.of("x")))
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
