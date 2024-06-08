package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Literals.TypeIdentifier.type;
import static io.zmeu.Frontend.Parser.Program.program;
import static io.zmeu.Frontend.Parser.Statements.BlockExpression.block;
import static io.zmeu.Frontend.Parser.Statements.FunctionDeclaration.fun;
import static io.zmeu.Frontend.Parser.Statements.ReturnStatement.funReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunTest extends BaseTest {

    @Test
    void testWithArgs() {
        var res = parse("""
                fun square(x) { 
                    return x*x
                }
                """);
        var expected = program(
                fun("square", List.of(type("x")), block(
                                funReturn(binary("*", "x", "x"))
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWith2Args() {
        var res = parse("""
                fun square(x,y) { 
                    return x*y
                }
                """);
        var expected = program(
                fun("square", List.of(type("x"), type("y")), block(
                                funReturn(
                                        binary("*", "x", "y")
                                )
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWithoutReturn() {
        var res = parse("""
                fun square(x) { 
                    return
                }
                """);
        var expected = program(
                fun("square", List.of(type("x")), block(
                                funReturn(ExpressionStatement.of())
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testWithoutParamsAndReturn() {
        var res = parse("""
                fun square() { 
                    return
                }
                """);
        var expected = program(
                fun("square", block(
                                funReturn(ExpressionStatement.of())
                        )
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testEmptyBody() {
        var res = parse("""
                fun square() { 
                }
                """);
        var expected = program(fun("square", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
