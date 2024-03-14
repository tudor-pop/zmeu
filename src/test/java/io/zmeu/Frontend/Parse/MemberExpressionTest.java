package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.AssignmentExpression;
import io.zmeu.Frontend.Parser.Expressions.MemberExpression;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.expressionStatement;
import static io.zmeu.Frontend.Parser.Factory.program;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class MemberExpressionTest extends BaseTest {

    @Test
    void testMember() {
        var res = parse("x.y");
        var expected = program(expressionStatement(
                MemberExpression.of(false, "x", "y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignment() {
        var res = parse("x.y = 1");
        var expected = program(expressionStatement(AssignmentExpression.of(
                MemberExpression.of(false, "x", "y"),
                1, "="))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignmentComputed() {
        var res = parse("x[0] = 1");
        var expected = program(expressionStatement(AssignmentExpression.of(
                MemberExpression.of(true, "x", 0),
                1, "="))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberComputedNested() {
        var res = parse("x.y.z['key']");
        var expected = program(expressionStatement(
                        MemberExpression.of(true,
                                MemberExpression.of(false,
                                        MemberExpression.of(false, "x", "y")
                                        , "z")
                                , StringLiteral.of("key"))
                )
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberComputedNestedAssignment() {
        var res = parse("x.y.z['key'] = 1");
        var expected = program(expressionStatement(
                AssignmentExpression.of("=",
                        MemberExpression.of(true,
                                MemberExpression.of(false,
                                        MemberExpression.of(false, "x", "y")
                                        , "z")
                                , StringLiteral.of("key"))
                        , 1)
        ));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

}