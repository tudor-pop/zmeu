package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.AssignmentExpression;
import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Expressions.LogicalExpression;
import io.zmeu.Frontend.Parser.Literals.BooleanLiteral;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LogicalTest extends BaseTest {

    @Test
    void testLogicalAnd() {
        var res = parse("x > 0 && y < 0");
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("&&",
                        BinaryExpression.of("x", 0, ">"),
                        BinaryExpression.of("y", 0, "<")
                )
        ));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalOr() {
        var res = parse("x > 0 || y < 0");
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("||",
                        BinaryExpression.of("x", 0, ">"),
                        BinaryExpression.of("y", 0, "<")
                )
        ));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogical() {
        var res = parse("x > 0 || y < 0 && z < 0");
        var expected = Program.of(ExpressionStatement.of(
                LogicalExpression.of("||",
                        BinaryExpression.of(">", "x", 0),
                        LogicalExpression.of("&&",
                                BinaryExpression.of("<", "y", 0),
                                BinaryExpression.of("<", "z", 0))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalOrEquals() {
        var res = parse("x = true || false");
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of("=", Identifier.of("x"),
                        LogicalExpression.of("||", BooleanLiteral.of(true), BooleanLiteral.of(false))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalAndEquals() {
        var res = parse("x = true && false");
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of("=", Identifier.of("x"),
                        LogicalExpression.of("&&", BooleanLiteral.of(true), BooleanLiteral.of(false))
                )));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

}
