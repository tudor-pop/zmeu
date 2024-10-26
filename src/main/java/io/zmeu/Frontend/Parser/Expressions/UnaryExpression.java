package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class UnaryExpression extends Expression {
    private Expression value;
    private String operator;

    public UnaryExpression() {
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
