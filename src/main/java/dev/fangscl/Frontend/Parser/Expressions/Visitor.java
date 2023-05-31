package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.LambdaExpression;

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

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    R eval(ResourceExpression expression);

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
