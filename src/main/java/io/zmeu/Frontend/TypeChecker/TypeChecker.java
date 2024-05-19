package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.types.Types;

public class TypeChecker implements Visitor<Types> {

    public TypeChecker() {
    }

    @Override
    public Types eval(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public Types eval(NumberLiteral expression) {
       return Types.Number;
    }

    @Override
    public Types eval(BooleanLiteral expression) {
        return Types.Boolean;
    }

    @Override
    public Types eval(Identifier expression) {
        return null;
    }

    @Override
    public Types eval(NullLiteral expression) {
        return null;
    }

    @Override
    public Types eval(StringLiteral expression) {
        return Types.String;
    }

    @Override
    public Types eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public Types eval(BlockExpression expression) {
        return null;
    }

    @Override
    public Types eval(GroupExpression expression) {
        return null;
    }

    @Override
    public Types eval(BinaryExpression expression) {
        return null;
    }

    @Override
    public Types eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public Types eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Types eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public Types eval(MemberExpression expression) {
        return null;
    }

    @Override
    public Types eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Types eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public Types eval(VariableDeclaration expression) {
        return null;
    }

    @Override
    public Types eval(AssignmentExpression expression) {
        return null;
    }

    @Override
    public Types eval(float expression) {
        return null;
    }

    @Override
    public Types eval(double expression) {
        return null;
    }

    @Override
    public Types eval(int expression) {
        return null;
    }

    @Override
    public Types eval(boolean expression) {
        return null;
    }

    @Override
    public Types eval(String expression) {
        return null;
    }
}
