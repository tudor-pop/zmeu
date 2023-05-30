package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Expressions.CallExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.LambdaExpression;
import dev.fangscl.Frontend.Parser.Statements.ReturnStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LambdaTest extends BaseTest {

    @Test
    void lambda() {
        var res = parser.produceAST(tokenizer.tokenize("""
                (x) -> x*x
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BinaryExpression.of("*", "x", "x")
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void lambdaTwoArgs() {
        var res = parser.produceAST(tokenizer.tokenize("""
                (x,y) -> x*y
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x"), Identifier.of("y")),
                                BinaryExpression.of("*", "x", "y")
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void lambdaBlock() {
        var res = parser.produceAST(tokenizer.tokenize("""
                (x,y) -> { x*y }
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x"), Identifier.of("y")),
                                BlockStatement.of(
                                        ExpressionStatement.of(
                                                BinaryExpression.of("*", "x", "y")
                                        )
                                )
                        )));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testWith2Args() {
        var res = parser.produceAST(tokenizer.tokenize("""
                (x) -> { 
                    return x*x
                }
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BlockStatement.of(
                                        ReturnStatement.of(
                                                BinaryExpression.of("*", "x", "x")
                                        )
                                )
                        )));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testWithoutReturn() {
        var res = parser.produceAST(tokenizer.tokenize("""
                (x) -> { 
                    return
                }
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BlockStatement.of(
                                        ReturnStatement.of(ExpressionStatement.of())
                                )
                        )));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testWithoutParamsAndReturn() {
        var res = parser.produceAST(tokenizer.tokenize("""
                () -> { 
                }
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(),
                                BlockStatement.of()
                        )));
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void callExpression() {
        var res = parser.produceAST(tokenizer.tokenize("""
                ((x) -> x*x)(2) 
                                
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        CallExpression.of(
                                LambdaExpression.of(
                                        List.of(Identifier.of("x")), BinaryExpression.of("*", "x", "x")
                                ),
                                2
                        )));
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionEmpty() {
        var res = parser.produceAST(tokenizer.tokenize("""
                ((x) -> x*x)(2)()
                                
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        CallExpression.of(
                                CallExpression.of(
                                        LambdaExpression.of(
                                                List.of(Identifier.of("x")), BinaryExpression.of("*", "x", "x")
                                        ),
                                        2
                                ), Collections.emptyList())
                ));
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionHi() {
        var res = parser.produceAST(tokenizer.tokenize("""
                ((x) -> x*x)(2)("hi")
                """));
        var expected = Program.of(
                ExpressionStatement.of(
                        CallExpression.of(
                                CallExpression.of(
                                        LambdaExpression.of(
                                                List.of(Identifier.of("x")), BinaryExpression.of("*", "x", "x")
                                        ),
                                        2
                                ), StringLiteral.of("hi"))
                ));
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

}
