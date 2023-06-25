package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Expressions.UnaryExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
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
}
