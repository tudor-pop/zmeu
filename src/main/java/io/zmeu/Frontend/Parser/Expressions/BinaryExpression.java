package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.BooleanLiteral;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BinaryExpression extends Expression {
    private Expression left;
    private Expression right;
    private String operator;

    public BinaryExpression() {
        this.kind = NodeType.BinaryExpression;
    }

    public BinaryExpression(Expression left, Expression right, String operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static Expression of(Expression left, Expression right, String operator) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression of(String operator, Expression left, Expression right) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression binary(String operator, Expression left, Expression right) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression of(Expression left, int right, String operator) {
        return of(left, NumberLiteral.of(right), operator);
    }

    public static Expression of(int left, Expression right, String operator) {
        return of(NumberLiteral.of(left), right, operator);
    }

    public static Expression of(Expression left, float right, String operator) {
        return of(left, NumberLiteral.of(right), operator);
    }

    public static Expression of(float left, Expression right, String operator) {
        return of(NumberLiteral.of(left), right, operator);
    }

    public static Expression of(Expression left, double right, String operator) {
        return of(left, NumberLiteral.of(right), operator);
    }

    public static Expression of(Expression left, boolean right, String operator) {
        return of(left, BooleanLiteral.of(right), operator);
    }

    public static Expression of(double left, Expression right, String operator) {
        return of(NumberLiteral.of(left), right, operator);
    }

    public static Expression of(int left, int right, String operator) {
        return of(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }
    public static Expression binary(int left, int right, String operator) {
        return of(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression of(String operator, int left, int right) {
        return of(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression of(float left, float right, String operator) {
        return of(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression of(double left, double right, String operator) {
        return of(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression of(String operator, String left, String right) {
        return of(operator, Identifier.of(left), Identifier.of(right));
    }

    public static Expression of(String operator, String left, int right) {
        return of(operator, Identifier.of(left), NumberLiteral.of(right));
    }
    public static Expression binary(String operator, String left, int right) {
        return of(operator, Identifier.of(left), NumberLiteral.of(right));
    }

    public static Expression of(String identifier, int left, String operator) {
        return of(operator, Identifier.of(identifier), NumberLiteral.of(left));
    }
    public static Expression binary(String identifier, int left, String operator) {
        return of(operator, Identifier.of(identifier), NumberLiteral.of(left));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
