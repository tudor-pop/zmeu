package dev.fangscl.Runtime.TypeSystem.Base;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Order of preccedence
 * AssignmentExpression
 * MemberExpression
 * FunctionCall
 * LogicalExpression
 * ComparisonExpression
 * AdditiveExpression
 * MultiplicativeExpression
 * LiteralExpression - Identity, integer, decimal
 * <p>
 * ExpressionStatement
 * : Expression
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionStatement extends Statement {
    private Expression expression;

    public ExpressionStatement(Expression expression) {
        this.kind = NodeType.ExpressionStatement;
        this.expression = expression;
    }

    public static Statement of(Expression expression) {
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
        return expression.toSExpression();
    }
}
