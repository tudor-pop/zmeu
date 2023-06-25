package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Expressions.CallExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
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
        var res = parse("(x) -> x*x");
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BinaryExpression.of("*", "x", "x")
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaTwoArgs() {
        var res = parse("(x,y) -> x*y");
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x"), Identifier.of("y")),
                                BinaryExpression.of("*", "x", "y")
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaBlock() {
        var res = parse("(x,y) -> { x*y }");
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x"), Identifier.of("y")),
                                BlockExpression.of(
                                        ExpressionStatement.of(
                                                BinaryExpression.of("*", "x", "y")
                                        )
                                )
                        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWith2Args() {
        var res = parse("""
                (x) -> { 
                    return x*x
                }
                """);
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BlockExpression.of(
                                        ReturnStatement.of(
                                                BinaryExpression.of("*", "x", "x")
                                        )
                                )
                        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWithoutReturn() {
        var res = parse("""
                (x) -> { 
                    return
                }
                """);
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(Identifier.of("x")),
                                BlockExpression.of(
                                        ReturnStatement.of(ExpressionStatement.of())
                                )
                        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWithoutParamsAndReturn() {
        var res = parse("""
                () -> { 
                }
                """);
        var expected = Program.of(
                ExpressionStatement.of(
                        LambdaExpression.of(List.of(),
                                BlockExpression.of()
                        )));
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void callExpression() {
        var res = parse("""
                ((x) -> x*x)(2) 
                                
                """);
        var expected = Program.of(
                ExpressionStatement.of(
                        CallExpression.of(
                                LambdaExpression.of(
                                        List.of(Identifier.of("x")), BinaryExpression.of("*", "x", "x")
                                ),
                                2
                        )));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionEmpty() {
        var res = parse("""
                ((x) -> x*x)(2)()
                                
                """);
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
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionHi() {
        var res = parse("""
                ((x) -> x*x)(2)("hi")
                """);
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
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

}
