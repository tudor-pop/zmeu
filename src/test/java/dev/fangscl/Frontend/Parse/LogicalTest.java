package dev.fangscl.Frontend.Parse;

import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.LogicalExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
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
        log.info(gson.toJson(res));
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
        log.info(gson.toJson(res));
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
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

}
