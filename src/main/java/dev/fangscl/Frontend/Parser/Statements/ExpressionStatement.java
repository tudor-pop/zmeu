package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An expression statement is one that evaluates an expression and ignores its result.
 * As a rule, an expression statement's purpose is to trigger the effects of evaluating its expression.
 * ExpressionStatement
 * : Expression '\n'
 * | Statement
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionStatement extends Statement {
    private Statement statement;

    private ExpressionStatement(Statement statement) {
        this.kind = NodeType.ExpressionStatement;
        this.statement = statement;
    }

    public static Statement of(Expression expression) {
        return new ExpressionStatement(expression);
    }
    public static Statement of(Statement expression) {
        return new ExpressionStatement(expression);
    }

    public static Statement of(int value) {
        return new ExpressionStatement(Literal.of(value));
    }

    public static Statement of(double value) {
        return new ExpressionStatement(Literal.of(value));
    }

    public static Statement of(float value) {
        return new ExpressionStatement(Literal.of(value));
    }

    public static Statement of(String value) {
        return new ExpressionStatement(Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return statement.toSExpression();
    }
}
