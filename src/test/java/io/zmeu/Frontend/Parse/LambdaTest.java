package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Expressions.CallExpression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.Frontend.Parser.Statements.ReturnStatement;
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
                                BinaryExpression.binary("*", "x", "x")
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
                                BinaryExpression.binary("*", "x", "y")
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
                                BlockExpression.block(
                                        ExpressionStatement.of(
                                                BinaryExpression.binary("*", "x", "y")
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
                                BlockExpression.block(
                                        ReturnStatement.funReturn(
                                                BinaryExpression.binary("*", "x", "x")
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
                                BlockExpression.block(
                                        ReturnStatement.funReturn(ExpressionStatement.of())
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
                                BlockExpression.block()
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
                                        List.of(Identifier.of("x")), BinaryExpression.binary("*", "x", "x")
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
                                                List.of(Identifier.of("x")), BinaryExpression.binary("*", "x", "x")
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
                                                List.of(Identifier.of("x")), BinaryExpression.binary("*", "x", "x")
                                        ),
                                        2
                                ), StringLiteral.of("hi"))
                ));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

}
