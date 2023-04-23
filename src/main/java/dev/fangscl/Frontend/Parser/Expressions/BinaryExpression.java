package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
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

    public static Expression of(Expression left, Expression right, Object operator) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression of(Object operator, Expression left, Expression right) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression of(Expression left, int right, Object operator) {
        return of(left, NumericLiteral.of(right), operator);
    }

    public static Expression of(int left, Expression right, Object operator) {
        return of(NumericLiteral.of(left), right, operator);
    }

    public static Expression of(Expression left, float right, Object operator) {
        return of(left, NumericLiteral.of(right), operator);
    }

    public static Expression of(float left, Expression right, Object operator) {
        return of(NumericLiteral.of(left), right, operator);
    }

    public static Expression of(Expression left, double right, Object operator) {
        return of(left, NumericLiteral.of(right), operator);
    }

    public static Expression of(Expression left, boolean right, Object operator) {
        return of(left, BooleanLiteral.of(right), operator);
    }

    public static Expression of(double left, Expression right, Object operator) {
        return of(NumericLiteral.of(left), right, operator);
    }

    public static Expression of(int left, int right, Object operator) {
        return of(NumericLiteral.of(left), NumericLiteral.of(right), operator);
    }

    public static Expression of(Object operator, int left, int right) {
        return of(NumericLiteral.of(left), NumericLiteral.of(right), operator);
    }

    public static Expression of(float left, float right, Object operator) {
        return of(NumericLiteral.of(left), NumericLiteral.of(right), operator);
    }

    public static Expression of(double left, double right, Object operator) {
        return of(NumericLiteral.of(left), NumericLiteral.of(right), operator);
    }

    public static Expression of(String operator, String left, String right) {
        return of(operator, Identifier.of(left), Identifier.of(right));
    }

    public static Expression of(String operator, String left, int right) {
        return of(operator, Identifier.of(left), NumericLiteral.of(right));
    }

    public static Expression of(String identifier, int left, String operator) {
        return of(operator, Identifier.of(identifier), NumericLiteral.of(left));
    }

    @Override
    public String toSExpression() {
        return "(" + operator + " " + left.toSExpression() + " " + right.toSExpression() + ")";
    }
}
