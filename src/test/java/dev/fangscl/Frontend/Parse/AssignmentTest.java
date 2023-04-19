package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.SyntaxError;
import dev.fangscl.Runtime.TypeSystem.Expressions.AssignmentExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class AssignmentTest extends BaseTest {

    @Test
    void testAssignment() {
        var res = parser.produceAST(tokenizer.tokenize("x=2"));
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of(Identifier.of("x"), NumericLiteral.of(2), "=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testMultipleAssignments() {
        var res = parser.produceAST(tokenizer.tokenize("x=y=2"));
        var expected = Program.of(ExpressionStatement.of(
                AssignmentExpression.of(Identifier.of("x"),
                        AssignmentExpression.of(Identifier.of("y"), NumericLiteral.of(2),
                                "="),
                        "=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testMultipleAssignments3() {
        var res = parser.produceAST(tokenizer.tokenize("x=y=z=2"));
        var expected = Program.of(
                ExpressionStatement.of(
                        AssignmentExpression.of(Identifier.of("x"),
                                AssignmentExpression.of(Identifier.of("y"),
                                        AssignmentExpression.of(Identifier.of("z"), NumericLiteral.of(2), "="), "="),
                                "="
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testVariableAddition() {
        var res = parser.produceAST(tokenizer.tokenize("x+x"));
        var expected = Program.of(
                ExpressionStatement.of(
                        BinaryExpression.of(Identifier.of("x"), Identifier.of("x"), "+")
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentOnlyToIdentifiers() {
        Assertions.assertThrows(SyntaxError.class, () ->
                parser.produceAST(tokenizer.tokenize("2=2"))
        );
    }


}
