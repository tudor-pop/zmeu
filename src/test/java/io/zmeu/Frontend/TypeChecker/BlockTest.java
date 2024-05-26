package io.zmeu.Frontend.TypeChecker;

import io.zmeu.types.Types;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Expressions.BinaryExpression.binary;
import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.var;
import static io.zmeu.Frontend.Parser.Literals.Identifier.id;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static io.zmeu.Frontend.Parser.Statements.BlockExpression.block;
import static io.zmeu.Frontend.Parser.Statements.ExpressionStatement.expressionStatement;
import static io.zmeu.Frontend.Parser.Statements.VariableStatement.statement;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockTest extends BaseChecker {

    @Test
    void testBlock() {
        var actual = checker.eval(block(
                statement(var("x", number(10))),
                statement(var("y", number(10))),
                expressionStatement(binary("+", binary("*", "x", 2), id("y")))
        ));
        assertEquals(Types.Number, actual);
    }

    @Test
    void testNestedBlock() {
        var actual = checker.eval(block(
                statement(var("x", number(10))),
                expressionStatement(block(statement(var("x", string("hello"))))),
                expressionStatement(binary("*", "x", 2))
        ));
        assertEquals(Types.Number, actual);
    }

    @Test
    void testScopeChainLookup() {
        var actual = checker.eval(src("""
                var x = 10
                {
                    var y = 10
                    x+y
                }
                """));
        assertEquals(Types.Number, actual);
    }
}
