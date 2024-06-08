package io.zmeu.Frontend.Parser;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Frontend.Parser.Statements.*;

import java.util.Arrays;

public class Factory {
    public static Program program(Statement... object) {
        return Program.builder().body(Arrays.stream(object).toList()).build();
    }

    public static Program program(Expression... object) {
        return Program.builder().body(Arrays.stream(object).map(ExpressionStatement::expressionStatement).toList()).build();
    }

    public static Statement expressionStatement(Expression object) {
        return ExpressionStatement.expressionStatement(object);
    }

    public static Expression unary(Object operator, Expression left) {
        return UnaryExpression.of(operator, left);
    }

    public static Expression unary(Object operator, String left) {
        return UnaryExpression.of(operator, Identifier.of(left));
    }

    public static Expression number(int number) {
        return NumberLiteral.number(number);
    }

    public static Expression binary(String operator, Expression left, Expression right) {
        return BinaryExpression.binary(operator, left, right);
    }

    public static Expression binary(String operator, Expression left, int right) {
        return BinaryExpression.binary(operator, left, NumberLiteral.of(right));
    }

    public static Expression binary(String identifier, int left, String operator) {
        return BinaryExpression.binary(operator, Identifier.of(identifier), NumberLiteral.of(left));
    }

    public static Expression greater(String identifier, int left) {
        return BinaryExpression.binary(">", Identifier.of(identifier), NumberLiteral.of(left));
    }

    public static Expression less(String identifier, int left) {
        return BinaryExpression.binary(">", Identifier.of(identifier), NumberLiteral.of(left));
    }

    public static Statement resource(String type, String name, BlockExpression operator) {
        return ResourceExpression.of(TypeIdentifier.of(type), Identifier.of(name), operator);
    }

    public static Expression assign(Expression type, Expression name) {
        return AssignmentExpression.assign("=", type, name);
    }

    public static Expression assign(String left, String right) {
        return AssignmentExpression.assign("=", Identifier.of(left), StringLiteral.of(right));
    }

    public static Expression assign(String left, Expression right) {
        return AssignmentExpression.assign("=", Identifier.of(left), right);
    }

    public static Expression assign(String type, int right) {
        return AssignmentExpression.assign("=", Identifier.of(type), NumberLiteral.of(right));
    }

    public static Expression member(String type, String right) {
        return MemberExpression.member(false, Identifier.of(type), Identifier.of(right));
    }

    public static BlockExpression block(Expression operator) {
        return (BlockExpression) BlockExpression.block(operator);
    }

    public static BlockExpression block(String operator) {
        return (BlockExpression) BlockExpression.block(operator);
    }

    public static VariableStatement var(Identifier id, TypeIdentifier type) {
        return (VariableStatement) VariableStatement.of(VariableDeclaration.of(id, type));
    }

    public static SchemaDeclaration schema(Identifier type, Statement statement) {
        return (SchemaDeclaration) SchemaDeclaration.of(type, statement);
    }

    public static SchemaDeclaration schema(Identifier type, Expression statement) {
        return (SchemaDeclaration) SchemaDeclaration.of(type, statement);
    }


    public static BlockExpression block(Statement operator) {
        return BlockExpression.block(operator);
    }

    public static BlockExpression block() {
        return (BlockExpression) BlockExpression.block();
    }

    public static Statement empty() {
        return EmptyStatement.of();
    }
}
