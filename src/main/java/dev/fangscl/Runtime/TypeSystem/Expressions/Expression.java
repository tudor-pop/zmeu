package dev.fangscl.Runtime.TypeSystem.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;

/**
 * Order of preccedence
 * AssignmentExpression
 * MemberExpression
 * FunctionCall
 * LogicalExpression
 * ComparisonExpression
 * AdditiveExpression
 * MultiplicativeExpression
 * LiteralExpression - Identity, integer, decimal
 * <p>
 * Expression
 * : Literal
 * ;
 */
public abstract class Expression extends Statement {

    public static Expression of(Expression left, Expression right, Object operator) {
        return new BinaryExpression(left, right, operator);
    }

    public static Expression of(Expression left, int right, Object operator) {
        return of(left, Literal.of(right), operator);
    }

    public static Expression of(int left, Expression right, Object operator) {
        return of(Literal.of(left), right, operator);
    }

    public static Expression of(Expression left, float right, Object operator) {
        return of(left, Literal.of(right), operator);
    }

    public static Expression of(float left, Expression right, Object operator) {
        return of(Literal.of(left), right, operator);
    }

    public static Expression of(Expression left, double right, Object operator) {
        return of(left, Literal.of(right), operator);
    }

    public static Expression of(double left, Expression right, Object operator) {
        return of(Literal.of(left), right, operator);
    }

    public static Expression of(int left, int right, Object operator) {
        return of(Literal.of(left), Literal.of(right), operator);
    }

    public static Expression of(float left, float right, Object operator) {
        return of(Literal.of(left), Literal.of(right), operator);
    }

    public static Expression of(double left, double right, Object operator) {
        return of(Literal.of(left), Literal.of(right), operator);
    }
}
