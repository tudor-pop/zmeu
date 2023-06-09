package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.AssignmentExpression;
import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Expressions.LogicalExpression;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LogicalTest extends BaseTest {

    @Test
    void testLogicalAnd() {
        var res = parser.produceAST(tokenizer.tokenize("x > 0 && y < 0"));
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("&&",
                        BinaryExpression.of("x", 0, ">"),
                        BinaryExpression.of("y", 0, "<")
                )
        ));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalOr() {
        var res = parser.produceAST(tokenizer.tokenize("x > 0 || y < 0"));
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("||",
                        BinaryExpression.of("x", 0, ">"),
                        BinaryExpression.of("y", 0, "<")
                )
        ));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogical() {
        var res = parser.produceAST(tokenizer.tokenize("x > 0 || y < 0 && z < 0"));
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("||",
                        BinaryExpression.of(">", "x", 0),
                        LogicalExpression.of("&&",
                                BinaryExpression.of("<", "y", 0),
                                BinaryExpression.of("<", "z", 0))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalOrEquals() {
        var res = parser.produceAST(tokenizer.tokenize("x = true || false"));
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of("=", Identifier.of("x"),
                        LogicalExpression.of("||", BooleanLiteral.of(true), BooleanLiteral.of(false))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalAndEquals() {
        var res = parser.produceAST(tokenizer.tokenize("x = true && false"));
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of("=", Identifier.of("x"),
                        LogicalExpression.of("&&", BooleanLiteral.of(true), BooleanLiteral.of(false))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

}
