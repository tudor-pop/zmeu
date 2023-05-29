package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Frontend.Parser.Statements.LambdaExpression;

public interface Visitor<R> {
    R visit(Expression expression);

    R visit(NumericLiteral expression);
    R visit(BooleanLiteral expression);
    R visit(Identifier expression);
    R visit(NullLiteral expression);
    R visit(StringLiteral expression);
    R visit(LambdaExpression expression);

    R visit(BlockStatement expression);
    R visit(GroupExpression expression);
    R visit(BinaryExpression expression);
    R visit(CallExpression expression);
    R visit(ErrorExpression expression);
    R visit(LogicalExpression expression);
    R visit(MemberExpression expression);
    R visit(ResourceExpression expression);
    R visit(ThisExpression expression);
    R visit(UnaryExpression expression);
    R visit(VariableDeclaration expression);
    R visit(AssignmentExpression expression);
    R visit(float expression);
    R visit(double expression);
    R visit(int expression);
    R visit(boolean expression);
    R visit(String expression);
}
