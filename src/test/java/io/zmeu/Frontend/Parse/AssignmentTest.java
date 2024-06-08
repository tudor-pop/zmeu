package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.AssignmentExpression;
import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class AssignmentTest extends BaseTest {

    @Test
    void testAssignment() {
        var res = parse("x=2");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                AssignmentExpression.of("=", Identifier.of("x"), NumberLiteral.of(2))));
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void testAssignmentBlock() {
        var res = parse("x={2}");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                AssignmentExpression.of("=", Identifier.of("x"), BlockExpression.block(ExpressionStatement.expressionStatement(NumberLiteral.of(2))))));
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void testAssignmentBlockWithStatements() {
        var res = parse("""
                x={
                    y=2
                    2
                }
                """);
        var expected = Program.of(ExpressionStatement.expressionStatement(
                AssignmentExpression.of("=", Identifier.of("x"),
                        BlockExpression.block(ExpressionStatement.expressionStatement(AssignmentExpression.of("=", Identifier.of("y"), NumberLiteral.of(2))),
                                ExpressionStatement.expressionStatement(NumberLiteral.of(2))))));
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testMultipleAssignments() {
        var res = parse("x=y=2");
        var expected = Program.of(ExpressionStatement.expressionStatement(
                AssignmentExpression.of("=", Identifier.of("x"),
                        AssignmentExpression.of("=", Identifier.of("y"), NumberLiteral.of(2))
                )));
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void testMultipleAssignments3() {
        var res = parse("x=y=z=2");
        var expected = Program.of(
                ExpressionStatement.expressionStatement(
                        AssignmentExpression.of("=", Identifier.of("x"),
                                AssignmentExpression.of("=", Identifier.of("y"),
                                        AssignmentExpression.of("=", Identifier.of("z"), NumberLiteral.of(2)))
                        )
                )
        );
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void testVariableAddition() {
        var res = parse("x+x");
        var expected = Program.of(
                ExpressionStatement.expressionStatement(
                        BinaryExpression.binary(Identifier.of("x"), Identifier.of("x"), "+")
                )
        );
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void testAssignmentOnlyToIdentifiers() {
        parse("2=2");
    }

    @Test
    void assignInvalid() {
        parse("1+2=10");
    }


    @Test
    void assignInvalidMember() {
        parse("a().x+1=10");
    }


}
