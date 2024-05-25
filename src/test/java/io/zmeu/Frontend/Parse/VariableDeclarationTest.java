package io.zmeu.Frontend.Parse;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Program.program;
import static io.zmeu.Frontend.Parser.Statements.BlockExpression.block;
import static io.zmeu.Frontend.Parser.Statements.ExpressionStatement.expressionStatement;
import static io.zmeu.Frontend.Parser.Statements.ExpressionStatement.of;
import static io.zmeu.Frontend.Parser.Statements.VariableStatement.statement;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class VariableDeclarationTest extends BaseTest {

    @Test
    void testDeclaration() {
        var res = parse("var x");
        var expected = program(
                statement(var("x")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarations() {
        var res = parse("var x,y");
        var expected = program(
                statement(
                        var("x"),
                        var("y")
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarationWithInit() {
        var res = parse("var x = 2");
        var expected = program(
                statement(
                        var("x", number(2))
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testDeclarationsWithValues() {
        var res = parse("var x,y=2");
        var expected = program(
                statement(
                        var("x"),
                        var("y", number(2))
                ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testInitWithValues() {
        var res = parse("var x=3,y=2");
        var expected = program(
                statement(
                        var("x", number(3)),
                        var("y", number(2))
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
        var expected = program(statement(
                var("x", block(expressionStatement(number(2)))))
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
        var expected = program(
                statement(
                        var("x", block(
                                statement(var("y", number(2))),
                                of(number(3)))))
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
        var expected = program(
                statement(var("x", number(3))),
                statement(var("y", number(2)))
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
        var expected = program(
                statement(var("x", number(3))),
                statement(var("y", number(2)))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
