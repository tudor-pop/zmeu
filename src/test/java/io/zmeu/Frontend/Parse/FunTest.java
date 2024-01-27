package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import io.zmeu.Frontend.Parser.Statements.FunctionDeclaration;
import io.zmeu.Frontend.Parser.Statements.ReturnStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(Identifier.of("x")),
                        BlockExpression.of(
                                ReturnStatement.of(
                                        BinaryExpression.of("*", "x", "x")
                                )
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
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(Identifier.of("x"), Identifier.of("y")),
                        BlockExpression.of(
                                ReturnStatement.of(
                                        BinaryExpression.of("*", "x", "y")
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
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(Identifier.of("x")),
                        BlockExpression.of(
                                ReturnStatement.of(ExpressionStatement.of())
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
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(),
                        BlockExpression.of(
                                ReturnStatement.of(ExpressionStatement.of())
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
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(),
                        BlockExpression.of()
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
