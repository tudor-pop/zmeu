package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UnaryExpression extends Expression {
    private Expression value;
    private Object operator;

    public UnaryExpression() {
        this.kind = NodeType.UnaryExpression;
    }

    public UnaryExpression(Expression left, Object operator) {
        this();
        this.value = left;
        this.operator = operator;
    }

    public static Expression of(Object operator, Expression left) {
        return new UnaryExpression(left, operator);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
    @Override
    public String toSExpression() {
        return "(" + operator + " " + value.toSExpression() + ")";
    }
}
