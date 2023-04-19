package dev.fangscl.Frontend.Parse;

import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.LogicalExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class RelationalTest extends BaseTest {

    @Test
    void testGreaterThan() {
        var res = parser.produceAST(tokenizer.tokenize("x>2"));
        var expected = Program.of(ExpressionStatement.of(BinaryExpression.of("x", 2, ">")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testGreaterThanEq() {
        var res = parser.produceAST(tokenizer.tokenize("x>=2"));
        var expected = Program.of(ExpressionStatement.of(BinaryExpression.of("x", 2, ">=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessThan() {
        var res = parser.produceAST(tokenizer.tokenize("x<2"));
        var expected = Program.of(ExpressionStatement.of(BinaryExpression.of("x", 2, "<")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessThanEq() {
        var res = parser.produceAST(tokenizer.tokenize("x<=2"));
        var expected = Program.of(ExpressionStatement.of(BinaryExpression.of("x", 2, "<=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessLowerPrecedenceThanAdditive() {
        var res = parser.produceAST(tokenizer.tokenize("x+2 > 10"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(BinaryExpression.of("x", 2, "+"), 10, ">")
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveTrue() {
        var res = parser.produceAST(tokenizer.tokenize("x > 2 == true"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of("x", 2, ">"), true, "==")
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveFalse() {
        var res = parser.produceAST(tokenizer.tokenize("x > 2 == false"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of("x", 2, ">"), false, "==")
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveNotFalse() {
        var res = parser.produceAST(tokenizer.tokenize("x > 2 != false"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of("x", 2, ">"), false, "!=")
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveNotTrue() {
        var res = parser.produceAST(tokenizer.tokenize("x > 2 != true"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of("x", 2, ">"), true, "!=")
        ));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

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

}
