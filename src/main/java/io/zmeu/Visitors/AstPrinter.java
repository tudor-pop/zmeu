package io.zmeu.Visitors;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.TypeChecker.Types.Type;

public final class AstPrinter implements Visitor<String> {

    public String print(Expression expr) {
        return eval(expr);
    }

    @Override
    public String eval(Expression expression) {
        return print(expression);
    }

    @Override
    public String eval(BinaryExpression expression) {
        return parenthesize(expression.getOperator(), expression.getLeft(), expression.getRight());
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
        return parenthesize(expression.getOperator().toString(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String eval(MemberExpression expression) {
        return null;
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
        return parenthesize(expression.getOperator().toString(), expression.getLeft(), expression.getRight());
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
    public String eval(Program program) {
        return "";
    }

    @Override
    public String eval(Statement statement) {
        return "";
    }

    @Override
    public String eval(Type statement) {
        return "";
    }

    @Override
    public String eval(InitStatement statement) {
        return "";
    }

    @Override
    public String eval(FunctionDeclaration statement) {
        return "";
    }

    @Override
    public String eval(ExpressionStatement statement) {
        return "";
    }

    @Override
    public String eval(VariableStatement statement) {
        return "";
    }

    @Override
    public String eval(IfStatement statement) {
        return "";
    }

    @Override
    public String eval(WhileStatement statement) {
        return "";
    }

    @Override
    public String eval(ForStatement statement) {
        return "";
    }

    @Override
    public String eval(SchemaDeclaration statement) {
        return "";
    }

    @Override
    public String eval(ReturnStatement statement) {
        return "";
    }

    @Override
    public String eval(ResourceExpression expression) {
        return "";
    }

    @Override
    public String eval(NumberLiteral expression) {
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
        return expression.string();
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

        builder.append("(").append(name);
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}