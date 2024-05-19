package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;

public class TypeChecker implements Visitor<TypeValue> {

    public TypeChecker() {
    }

    @Override
    public TypeValue eval(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public TypeValue eval(NumericLiteral expression) {
        if (expression.isInteger()) {
            return TypeValue.INTEGER;
        } else if (expression.isDecimal()) {
            return TypeValue.DECIMAL;
        } else {
            throw new TypeError(expression);
        }
    }

    @Override
    public TypeValue eval(BooleanLiteral expression) {
        return TypeValue.BOOLEAN;
    }

    @Override
    public TypeValue eval(Identifier expression) {
        return null;
    }

    @Override
    public TypeValue eval(NullLiteral expression) {
        return null;
    }

    @Override
    public TypeValue eval(StringLiteral expression) {
        return null;
    }

    @Override
    public TypeValue eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(BlockExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(GroupExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(BinaryExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public TypeValue eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(MemberExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(ThisExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(VariableDeclaration expression) {
        return null;
    }

    @Override
    public TypeValue eval(AssignmentExpression expression) {
        return null;
    }

    @Override
    public TypeValue eval(float expression) {
        return null;
    }

    @Override
    public TypeValue eval(double expression) {
        return null;
    }

    @Override
    public TypeValue eval(int expression) {
        return null;
    }

    @Override
    public TypeValue eval(boolean expression) {
        return null;
    }

    @Override
    public TypeValue eval(String expression) {
        return null;
    }
}
