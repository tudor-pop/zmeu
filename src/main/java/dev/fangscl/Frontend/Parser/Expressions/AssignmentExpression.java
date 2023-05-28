package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
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
        return new AssignmentExpression(left, NumericLiteral.of(right), operator);
    }
    public static Expression of(Object operator, Expression left, Expression right) {
        return new AssignmentExpression(left, right, operator);
    }
    public static Expression of(Object operator, Expression left, String right) {
        return new AssignmentExpression(left, Identifier.of(right), operator);
    }
    public static Expression of(Object operator, Expression left, int right) {
        return new AssignmentExpression(left, NumericLiteral.of(right), operator);
    }


    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
