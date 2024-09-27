package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.BooleanLiteral;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
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

    public static Expression binary(Expression left, Expression right, String operator) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression binary(String operator, Expression left, Expression right) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression binary(Expression left, int right, String operator) {
        return binary(left, NumberLiteral.of(right), operator);
    }

    public static Expression binary(int left, Expression right, String operator) {
        return binary(NumberLiteral.of(left), right, operator);
    }

    public static Expression binary(Expression left, float right, String operator) {
        return binary(left, NumberLiteral.of(right), operator);
    }

    public static Expression binary(float left, Expression right, String operator) {
        return binary(NumberLiteral.of(left), right, operator);
    }

    public static Expression binary(Expression left, double right, String operator) {
        return binary(left, NumberLiteral.of(right), operator);
    }

    public static Expression binary(Expression left, boolean right, String operator) {
        return binary(left, BooleanLiteral.of(right), operator);
    }

    public static Expression binary(double left, Expression right, String operator) {
        return binary(NumberLiteral.of(left), right, operator);
    }

    public static Expression binary(int left, int right, String operator) {
        return binary(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression binary(String operator, int left, int right) {
        return binary(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression binary(float left, float right, String operator) {
        return binary(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression binary(double left, double right, String operator) {
        return binary(NumberLiteral.of(left), NumberLiteral.of(right), operator);
    }

    public static Expression binary(String operator, String left, String right) {
        return binary(operator, Identifier.id(left), Identifier.id(right));
    }

    public static Expression binary(String operator, String left, int right) {
        return binary(operator, Identifier.id(left), NumberLiteral.of(right));
    }

    public static Expression binary(String identifier, int left, String operator) {
        return binary(operator, Identifier.id(identifier), NumberLiteral.of(left));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
