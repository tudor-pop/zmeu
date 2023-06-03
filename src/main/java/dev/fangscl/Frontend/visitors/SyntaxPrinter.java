package dev.fangscl.Frontend.visitors;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.LambdaExpression;

public class SyntaxPrinter implements Visitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String eval(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public String eval(BinaryExpression expression) {
        return "%s %s %s".formatted(expression.getLeft().accept(this), expression.getOperator(), expression.getRight().accept(this));
    }

    @Override
    public String eval(CallExpression expression) {
        return null;
    }

    @Override
    public String eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public String eval(LogicalExpression expression) {
        return "%s %s %s".formatted(expression.getLeft(), expression.getOperator(), expression.getRight());
    }

    @Override
    public String eval(MemberExpression expression) {
        return null;
    }

    @Override
    public String eval(ResourceExpression expression) {
        return "resource " + expression.getName().accept(this) + expression.getBlock().accept(this);
    }

    @Override
    public String eval(ThisExpression expression) {
        return null;
    }

    @Override
    public String eval(UnaryExpression expression) {
        return parenthesize(expression.getOperator(), expression.getValue());
    }

    @Override
    public String eval(VariableDeclaration expression) {
        return null;
    }

    @Override
    public String eval(AssignmentExpression expression) {
        return expression.getLeft().accept(this) + expression.getOperator() + expression.getRight().accept(this);
    }

    @Override
    public String eval(float expression) {
        return String.valueOf(expression);
    }

    @Override
    public String eval(double expression) {
        return String.valueOf(expression);
    }

    @Override
    public String eval(int expression) {
        return String.valueOf(expression);
    }

    @Override
    public String eval(boolean expression) {
        return String.valueOf(expression);
    }

    @Override
    public String eval(String expression) {
        return String.valueOf(expression);
    }

    @Override
    public String eval(NumericLiteral expression) {
        if (expression.getVal() == null) {
            return "null";
        }
        return expression.getVal().toString();
    }

    @Override
    public String eval(BooleanLiteral expression) {
        return expression.getVal().toString();
    }

    @Override
    public String eval(Identifier expression) {
        return expression.getSymbol();
    }

    @Override
    public String eval(NullLiteral expression) {
        return "null";
    }

    @Override
    public String eval(StringLiteral expression) {
        return expression.getValue();
    }

    @Override
    public String eval(BlockExpression expression) {
        return null;
    }

    @Override
    public String eval(GroupExpression expression) {
        return parenthesize("group", expression.getExpression());
    }

    @Override
    public String eval(LambdaExpression expression) {
        return null;
    }

    private String parenthesize(String name, Expression... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}