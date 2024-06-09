package io.zmeu.Frontend.visitors;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.Types.Type;

public interface Visitor<R> {
    R eval(Expression expression);

    R eval(NumberLiteral expression);

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

    R eval(Program program);

    R eval(Statement statement);

    R eval(Type type);

    /**
     * InitStatement
     * Syntactic sugar for a function
     */
    R eval(InitStatement statement);

    R eval(FunctionDeclaration statement);

    R eval(ExpressionStatement statement);

    R eval(VariableStatement statement);

    R eval(IfStatement statement);

    R eval(WhileStatement statement);

    R eval(ForStatement statement);

    R eval(SchemaDeclaration statement);

    R eval(ReturnStatement statement);

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    R eval(ResourceExpression expression);
}
