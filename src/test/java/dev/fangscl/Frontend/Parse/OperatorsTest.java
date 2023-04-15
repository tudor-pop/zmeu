package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class OperatorsTest extends StatementTest {


    @Test
    void testAddition() {
        var res = parser.produceAST(tokenizer.tokenize("1 + 1"));

        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(1, 1, "+")));
        assertEquals(expected, res);
        assertEquals("(+ 1 1)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionMultipleLines() {
        var res = parser.produceAST(tokenizer.tokenize("""
                1 + 1
                2+2
                """));

        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(1, 1, "+")),
                ExpressionStatement.of(
                        BinaryExpression.of(2, 2, "+")));
        assertEquals(expected, res);
        assertEquals("(+ 1 1)(+ 2 2)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testSubstraction() {
        var res = parser.produceAST(tokenizer.tokenize("1-1"));

        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(1, 1, "-")));
        assertEquals(expected, res);
        assertEquals("(- 1 1)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testMultiplication() {
        var res = parser.produceAST(tokenizer.tokenize("1*1"));

        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(1, 1, "*")));
        assertEquals(expected, res);
        assertEquals("(* 1 1)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testDivision() {
        var res = parser.produceAST(tokenizer.tokenize("1/1"));

        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(1, 1, "/")));
        assertEquals(expected, res);
        assertEquals("(/ 1 1)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testAddition3() {
        var res = parser.produceAST(tokenizer.tokenize("1 + 1+1"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of(1, 1, "+"),
                        Literal.of(1),
                        "+"))
        );
        assertEquals(expected, res);
        assertEquals("(+ (+ 1 1) 1)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionSubstraction3() {
        var res = parser.produceAST(tokenizer.tokenize("1 + 1-11"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of(1, 1, "+"),
                        11,
                        "-"))
        );
        assertEquals(expected, res);
        assertEquals("(- (+ 1 1) 11)", res.toSExpression());
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionMultiplication() {
        var res = parser.produceAST(tokenizer.tokenize("1 + 2*3"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(
                                1,
                                BinaryExpression.of(2, 3, "*"),
                                "+"))
        );
        assertEquals("(+ 1 (* 2 3))", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionMultiplicationChangeOrder() {
        var res = parser.produceAST(tokenizer.tokenize("(1 + 2) * 3"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(
                                BinaryExpression.of(1, 2, "+"),
                                3, "*")
                ));

        assertEquals("(* (+ 1 2) 3)", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionMultiplicationChangeOrder2() {
        var res = parser.produceAST(tokenizer.tokenize("3 * (1 + 2)"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(3,
                                BinaryExpression.of(1, 2, "+"),
                                "*")
                ));

        assertEquals("(* 3 (+ 1 2))", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAdditionParanthesis() {
        var res = parser.produceAST(tokenizer.tokenize("1 + 2 - (3*4)"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(
                                BinaryExpression.of(1, 2, "+"),
                                BinaryExpression.of(3, 4, "*"),
                                "-"))
        );
        assertEquals("(- (+ 1 2) (* 3 4))", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void testMultiplicationWithParanthesis() {
        var res = parser.produceAST(tokenizer.tokenize("1 * 2 - (3*4)"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(
                                BinaryExpression.of(1, 2, "*"),
                                BinaryExpression.of(3, 4, "*"),
                                "-"))
        );
        assertEquals("(- (* 1 2) (* 3 4))", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testDivisionWithParanthesis() {
        var res = parser.produceAST(tokenizer.tokenize("1 / 2 - (3/4)"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(
                                BinaryExpression.of(1, 2, "/"),
                                BinaryExpression.of(3, 4, "/"),
                                "-"))
        );
        assertEquals("(- (/ 1 2) (/ 3 4))", res.toSExpression());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
