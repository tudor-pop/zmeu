package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LogicalExpression extends Expression {
    private Expression left;
    private Expression right;
    private Object operator;

    public LogicalExpression() {
        this.kind = NodeType.LogicalExpression;
    }

    public LogicalExpression(Expression left, Expression right, Object operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static Expression of(Object operator, Expression left, Expression right) {
        return new LogicalExpression(left, right, operator);
    }

    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }
}
