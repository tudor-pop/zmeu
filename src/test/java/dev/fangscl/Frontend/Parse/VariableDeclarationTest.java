package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.VariableStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class VariableDeclarationTest extends BaseTest {

    @Test
    void testDeclaration() {
        var res = parser.produceAST(tokenizer.tokenize("var x"));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testDeclarations() {
        var res = parser.produceAST(tokenizer.tokenize("var x,y"));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x")),
                        VariableDeclaration.of(Identifier.of("y"))
                ));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testDeclarationWithInit() {
        var res = parser.produceAST(tokenizer.tokenize("var x = 2"));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(2))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testDeclarationsWithValues() {
        var res = parser.produceAST(tokenizer.tokenize("var x,y=2"));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x")),
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                ));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testInitWithValues() {
        var res = parser.produceAST(tokenizer.tokenize("var x=3,y=2"));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(3)),
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                ));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void initVarWithBlockExpression() {
        var res = parser.produceAST(tokenizer.tokenize("""
                var x={
                    2
                }
                """));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), BlockExpression.of(
                                ExpressionStatement.of(NumericLiteral.of(2)))))
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testAssignmentBlockWithStatements() {
        var res = parser.produceAST(tokenizer.tokenize("""
                var x={
                    var y=2
                    2
                }
                """));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), BlockExpression.of(
                                VariableStatement.of(VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))),
                                ExpressionStatement.of(NumericLiteral.of(2)))))
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void multiVarInitWithValues() {
        var res = parser.produceAST(tokenizer.tokenize("""
                var x=3
                var y=2
                """));
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(3))),
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
