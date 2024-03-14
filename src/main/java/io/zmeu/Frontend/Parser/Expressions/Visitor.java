package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;

public interface Visitor<R> {
    R eval(Expression expression);

    R eval(NumericLiteral expression);

    R eval(BooleanLiteral expression);

    R eval(Identifier expression);

    R eval(NullLiteral expression);

    R eval(StringLiteral expression);

    R eval(LambdaExpression expression);

    R eval(BlockExpression expression);

    R eval(GroupExpression expression);

    R eval(BinaryExpression expression);

    R eval(CallExpression<Expression> expression);

    R eval(ErrorExpression expression);

    R eval(LogicalExpression expression);

    R eval(MemberExpression expression);

    R eval(ThisExpression expression);

    R eval(UnaryExpression expression);

    R eval(VariableDeclaration expression);

    R eval(AssignmentExpression expression);

    R eval(float expression);

    R eval(double expression);

    R eval(int expression);

    R eval(boolean expression);

    R eval(String expression);
}