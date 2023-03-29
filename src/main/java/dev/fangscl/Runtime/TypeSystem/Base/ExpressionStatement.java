package dev.fangscl.Runtime.TypeSystem.Base;

import dev.fangscl.Frontend.Parser.NodeType;
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
}
