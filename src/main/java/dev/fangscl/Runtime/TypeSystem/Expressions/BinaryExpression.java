package dev.fangscl.Runtime.TypeSystem.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BinaryExpression extends Expression {
    private Expression left;
    private Expression right;
    private Object operator;

    public BinaryExpression() {
        this.kind = NodeType.BinaryExpression;
    }

    public BinaryExpression(Expression left, Expression right, Object operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }
}
