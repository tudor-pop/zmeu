package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LogicalExpression extends Expression {
    private Expression left;
    private Expression right;
    private String operator;

    public LogicalExpression() {
        this.kind = NodeType.LogicalExpression;
    }

    public LogicalExpression(Expression left, Expression right, String operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static Expression of(Object operator, Expression left, Expression right) {
        return new LogicalExpression(left, right, operator.toString());
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }
}
