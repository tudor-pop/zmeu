package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UnaryExpression extends Expression {
    private Expression value;
    private String operator;

    public UnaryExpression() {
        this.kind = NodeType.UnaryExpression;
    }

    public UnaryExpression(Expression left, Object operator) {
        this();
        this.value = left;
        this.operator = operator.toString();
    }

    public static Expression of(Object operator, Expression left) {
        return new UnaryExpression(left, operator);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
