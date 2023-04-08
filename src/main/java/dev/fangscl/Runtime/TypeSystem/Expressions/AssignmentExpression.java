package dev.fangscl.Runtime.TypeSystem.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AssignmentExpression extends Expression {
    private Expression left;
    private Expression right;
    private String operator;

    public AssignmentExpression() {
        this.kind = NodeType.AssignmentExpression;
    }

    public AssignmentExpression(Expression left, Expression right) {
        this();
        this.left = left;
        this.right = right;
        this.operator = "=";
    }

    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }
}
