package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import io.zmeu.Frontend.Parser.Statements.VariableStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class VariableDeclarationTest extends BaseTest {

    @Test
    void testDeclaration() {
        var res = parse("var x");
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarations() {
        var res = parse("var x,y");
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x")),
                        VariableDeclaration.of(Identifier.of("y"))
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarationWithInit() {
        var res = parse("var x = 2");
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(2))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarationsWithValues() {
        var res = parse("var x,y=2");
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x")),
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testInitWithValues() {
        var res = parse("var x=3,y=2");
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(3)),
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


    @Test
    void initVarWithBlockStatement() {
        var res = parse("""
                var x={
                    2
                }
                """);
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), BlockExpression.of(
                                ExpressionStatement.of(NumericLiteral.of(2)))))
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testAssignmentBlockWithStatements() {
        var res = parse("""
                var x={
                    var y=2
                    3
                }
                """);
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), BlockExpression.of(
                                VariableStatement.of(VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))),
                                ExpressionStatement.of(NumericLiteral.of(3)))))
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void multiVarInitWithValues() {
        var res = parse("""
                var x=3
                var y=2
                """);
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(3))),
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void multiVarInitWithValuesAndLineterminator() {
        var res = parse("""
                var x=3;
                var y=2;
                """);
        var expected = Program.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(3))),
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("y"), NumericLiteral.of(2))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
