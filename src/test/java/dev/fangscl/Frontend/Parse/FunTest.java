package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.FunctionDeclaration;
import dev.fangscl.Frontend.Parser.Statements.ReturnStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunTest extends BaseTest {

    @Test
    void testWithArgs() {
        var res = parser.produceAST(tokenizer.tokenize("""
                fun square(x) { 
                    return x*x
                }
                """));
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
        log.info(gson.toJson(res));
    }

    @Test
    void testWith2Args() {
        var res = parser.produceAST(tokenizer.tokenize("""
                fun square(x,y) { 
                    return x*y
                }
                """));
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
        log.info(gson.toJson(res));
    }

    @Test
    void testWithoutReturn() {
        var res = parser.produceAST(tokenizer.tokenize("""
                fun square(x) { 
                    return
                }
                """));
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(Identifier.of("x")),
                        BlockExpression.of(
                                ReturnStatement.of(ExpressionStatement.of())
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testWithoutParamsAndReturn() {
        var res = parser.produceAST(tokenizer.tokenize("""
                fun square() { 
                    return
                }
                """));
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(),
                        BlockExpression.of(
                                ReturnStatement.of(ExpressionStatement.of())
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testEmptyBody() {
        var res = parser.produceAST(tokenizer.tokenize("""
                fun square() { 
                }
                """));
        var expected = Program.of(
                FunctionDeclaration.of(Identifier.of("square"),
                        List.of(),
                        BlockExpression.of()
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
