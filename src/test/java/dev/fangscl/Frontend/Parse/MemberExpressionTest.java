package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Expressions.AssignmentExpression;
import dev.fangscl.Frontend.Parser.Expressions.MemberExpression;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class MemberExpressionTest extends BaseTest {

    @Test
    void testMember() {
        var res = parser.produceAST(tokenizer.tokenize("x.y"));
        var expected = Program.of(ExpressionStatement.of(
                MemberExpression.of(false, "x", "y")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignment() {
        var res = parser.produceAST(tokenizer.tokenize("x.y = 1"));
        var expected = Program.of(ExpressionStatement.of(AssignmentExpression.of(
                MemberExpression.of(false, "x", "y"),
                1, "="))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberAssignmentComputed() {
        var res = parser.produceAST(tokenizer.tokenize("x[0] = 1"));
        var expected = Program.of(ExpressionStatement.of(AssignmentExpression.of(
                MemberExpression.of(true, "x", 0),
                1, "="))
        );
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testMemberComputedNested() {
        var res = parser.produceAST(tokenizer.tokenize("x.y.z['key']"));
        var expected = Program.of(ExpressionStatement.of(
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
        var res = parser.produceAST(tokenizer.tokenize("x.y.z['key'] = 1"));
        var expected = Program.of(ExpressionStatement.of(
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
