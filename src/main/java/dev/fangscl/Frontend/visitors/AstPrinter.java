package dev.fangscl.Frontend.visitors;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Frontend.Parser.Statements.LambdaExpression;

public class AstPrinter implements Visitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visit(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public String visit(BinaryExpression expression) {
        return parenthesize(expression.getOperator(), expression.getLeft(), expression.getRight());
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
        return parenthesize(expression.getOperator(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String visit(MemberExpression expression) {
        return null;
    }

    @Override
    public String visit(ResourceExpression expression) {
        return parenthesize("resource", expression.getName(), expression.getBlock());
    }

    @Override
    public String visit(ThisExpression expression) {
        return null;
    }

    @Override
    public String visit(UnaryExpression expression) {
        return parenthesize(expression.getOperator(), expression.getValue());
    }

    @Override
    public String visit(VariableDeclaration expression) {
        return null;
    }

    @Override
    public String visit(AssignmentExpression expression) {
        return parenthesize(expression.getOperator().toString(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String visit(NumericLiteral expression) {
        if (expression.getVal() == null) {
            return "null";
        }
        return expression.getVal().toString();
    }

    @Override
    public String visit(BooleanLiteral expression) {
        return expression.getVal().toString();
    }

    @Override
    public String visit(Identifier expression) {
        return expression.getSymbol();
    }

    @Override
    public String visit(NullLiteral expression) {
        return "null";
    }

    @Override
    public String visit(StringLiteral expression) {
        return expression.getValue();
    }

    @Override
    public String visit(BlockStatement expression) {
        return null;
    }

    @Override
    public String visit(GroupExpression expression) {
        return parenthesize("group", expression.getExpression());
    }

    @Override
    public String visit(LambdaExpression expression) {
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