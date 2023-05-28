package dev.fangscl.Frontend.Parser.Expressions;

public interface Visitor<R> {
    R visit(Expression expression);
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
}
