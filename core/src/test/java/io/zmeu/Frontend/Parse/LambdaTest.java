package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Literals.ParameterIdentifier;
import io.zmeu.TypeChecker.Types.ValueType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Expressions.CallExpression.call;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.type;
import static io.zmeu.Frontend.Parser.Program.program;
import static io.zmeu.Frontend.Parser.Statements.BlockExpression.block;
import static io.zmeu.Frontend.Parser.Statements.ExpressionStatement.expressionStatement;
import static io.zmeu.Frontend.Parser.Statements.LambdaExpression.lambda;
import static io.zmeu.Frontend.Parser.Statements.ReturnStatement.funReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("Parser Lambda")
public class LambdaTest extends BaseTest {

    @Test
    void lambdaSimple() {
        var res = parse("(x) -> x*x");
        var expected = program(expressionStatement(
                        lambda("x", binary("*", "x", "x"))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaArgTypeWithReturnType() {
        var res = parse("(x Number) Number -> x*x");
        var expected = program(expressionStatement(
                        lambda(ParameterIdentifier.param("x", type(ValueType.Number)), binary("*", "x", "x"), type(ValueType.Number))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaNoReturn() {
        var res = parse("""
                (x Number) -> {
                    print(x)
                }
                """);
        var expected = program(expressionStatement(
                        lambda(ParameterIdentifier.param("x", type(ValueType.Number)),
                                block(
                                        expressionStatement(
                                                call("print","x")
                                        )
                                ))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaTwoArgs() {
        var res = parse("(x,y) -> x*y");
        var expected = program(
                expressionStatement(lambda("x", "y", binary("*", "x", "y")))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void lambdaBlock() {
        var res = parse("(x,y) -> { x*y }");
        var expected = program(
                expressionStatement(
                        lambda("x", "y", block(expressionStatement(binary("*", "x", "y"))))
                ));
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
        var expected = program(expressionStatement(lambda("x", block(
                        funReturn(binary("*", "x", "x"))
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
        var expected = program(expressionStatement(lambda("x", block(
                        funReturn(expressionStatement(type(ValueType.Void)))
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
        var expected = program(expressionStatement(lambda(List.of(), block())));
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void callExpression() {
        var res = parse("""
                ((x) -> x*x)(2) 
                
                """);
        var expected = program(
                expressionStatement(call(lambda("x", binary("*", "x", "x")), 2)));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionEmpty() {
        var res = parse("""
                ((x) -> x*x)(2)()
                
                """);
        var expected = program(
                expressionStatement(
                        call(call(lambda("x", binary("*", "x", "x")), 2), Collections.emptyList())
                ));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void callExpressionHi() {
        var res = parse("""
                ((x) -> x*x)(2)("hi")
                """);
        var expected = program(
                expressionStatement(
                        call(call(lambda("x", binary("*", "x", "x")), 2), string("hi"))
                ));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

}
