package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.EmptyStatement;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;

import java.util.Arrays;

public class Factory {
    public static Program program(Statement... object) {
        return Program.builder().body(Arrays.stream(object).toList()).build();
    }

    public static Program program(Expression... object) {
        return Program.builder().body(Arrays.stream(object).map(ExpressionStatement::of).toList()).build();
    }

    public static Statement expressionStatement(Expression object) {
        return ExpressionStatement.of(object);
    }

    public static Expression unary(Object operator, Expression left) {
        return UnaryExpression.of(operator, left);
    }

    public static Expression unary(Object operator, String left) {
        return UnaryExpression.of(operator, Identifier.of(left));
    }

    public static Expression binary(String operator, Expression left, Expression right) {
        return BinaryExpression.of(operator, left, right);
    }

    public static Expression binary(String operator, Expression left, int right) {
        return BinaryExpression.of(operator, left, NumericLiteral.of(right));
    }

    public static Expression binary(String identifier, int left, String operator) {
        return BinaryExpression.of(operator, Identifier.of(identifier), NumericLiteral.of(left));
    }

    public static Statement resource(String type, String name, BlockExpression operator) {
        return ResourceExpression.of(Identifier.of(type), Identifier.of(name), operator);
    }

    public static Expression assign(Expression type, Expression name) {
        return AssignmentExpression.of("=", type, name);
    }

    public static Expression assign(String type, String name) {
        return AssignmentExpression.of("=", Identifier.of(type), StringLiteral.of(name));
    }

    public static BlockExpression block(Expression operator) {
        return (BlockExpression) BlockExpression.of(operator);
    }

    public static BlockExpression block(String operator) {
        return (BlockExpression) BlockExpression.of(operator);
    }

    public static BlockExpression block(Statement operator) {
        return BlockExpression.of(operator);
    }

    public static BlockExpression block() {
        return (BlockExpression) BlockExpression.of();
    }

    public static Statement empty() {
        return EmptyStatement.of();
    }
}
