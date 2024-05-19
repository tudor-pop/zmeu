package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssignmentExpression extends Expression {
    /**
     * Must either be Identifier or MemberExpression
     */
    private Expression left;
    private Expression right;
    private Object operator;

    private AssignmentExpression() {
        this.kind = NodeType.AssignmentExpression;
    }

    private AssignmentExpression(Expression left, Expression right, Object operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static Expression of(Expression left, Expression right, Object operator) {
        return new AssignmentExpression(left, right, operator);
    }
    public static Expression of(Expression left, int right, Object operator) {
        return new AssignmentExpression(left, NumberLiteral.of(right), operator);
    }
    public static Expression of(Object operator, Expression left, Expression right) {
        return new AssignmentExpression(left, right, operator);
    }
    public static Expression of(Object operator, Expression left, String right) {
        return new AssignmentExpression(left, Identifier.of(right), operator);
    }
    public static Expression of(Object operator, Expression left, int right) {
        return new AssignmentExpression(left, NumberLiteral.of(right), operator);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
