package dev.fangscl.Frontend.visitors;

import dev.fangscl.Frontend.Parser.Expressions.*;

class AstPrinter implements Visitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visit(Expression expression) {
        return null;
    }

    @Override
    public String visit(BinaryExpression expression) {
        return parenthesize(expression.getOperator().toString(),
                expression.getLeft(), expression.getRight());
    }

    @Override
    public String visit(CallExpression expression) {
        return null;
    }

    @Override
    public String visit(ErrorExpression expression) {
        return null;
    }

    @Override
    public String visit(LogicalExpression expression) {
        return null;
    }

    @Override
    public String visit(MemberExpression expression) {
        return null;
    }

    @Override
    public String visit(ResourceExpression expression) {
        return null;
    }

    @Override
    public String visit(ThisExpression expression) {
        return null;
    }

    @Override
    public String visit(UnaryExpression expression) {
        return null;
    }

    @Override
    public String visit(VariableDeclaration expression) {
        return null;
    }

    @Override
    public String visit(AssignmentExpression expression) {
        return null;
    }

    private String parenthesize(String name, Expression... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}