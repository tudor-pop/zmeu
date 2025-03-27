package io.zmeu.Visitors;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.TypeChecker.Types.Type;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;


public non-sealed class LanguageAstPrinter implements Visitor<String> {
    private static final Ansi ansi = Ansi.ansi();

    public String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String eval(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public String eval(BinaryExpression expression) {
        return eval(expression.getLeft()) + " " + expression.getOperator() + " " + eval(expression.getRight());
    }

    @Override
    public String eval(CallExpression<Expression> expression) {
        var callName = eval(expression.getCallee());
        var args = expression.getArguments()
                .stream()
                .map(this::eval)
                .collect(Collectors.joining(","));
        return callName + "(" + args + ")";
    }

    @Override
    public String eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public String eval(LogicalExpression expression) {
        return "(" + eval(expression.getLeft()) + " " + expression.getOperator().toString() + " " + eval(expression.getRight()) + ")";
    }

    @Override
    public String eval(MemberExpression expression) {
        return eval(expression.getObject()) + "." + eval(expression.getProperty());
    }

    @Override
    public String eval(ThisExpression expression) {
        return "this." + eval(expression.getInstance());
    }

    @Override
    public String eval(UnaryExpression expression) {
        return parenthesize(expression.getOperator(), expression.getValue());
    }

    @Override
    public String eval(VariableDeclaration expression) {
        StringBuilder var = new StringBuilder("var " + expression.getId().string());
        if (expression.hasType()) {
            var.append(" :").append(expression.getType().getType().getValue());
        }
        if (expression.hasInit()) {
            var.append(" = ").append(eval(expression.getInit()));
        }
        return var.toString();
    }

    @Override
    public String eval(AssignmentExpression expression) {
        return eval(expression.getLeft()) + " " + expression.getOperator().toString() + " " + eval(expression.getRight());
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
        return program.getBody()
                .stream()
                .map(this::eval)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String eval(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public String eval(Type type) {
        return type.getValue();
    }

    @Override
    public String eval(InitStatement statement) {
        return "";
    }

    @Override
    public String eval(FunctionDeclaration statement) {
        return "fun " +
                statement.getName().string() +
                "("
                + statement.getParams().stream().map(LanguageAstPrinter::formatParameter).collect(Collectors.joining(","))
                + ") "
                + "{ \n"
                + eval(statement.getBody())
                + "\n} \n";
    }

    private static @NotNull String formatParameter(ParameterIdentifier it) {
        if (it.getType() == null || it.getType().getType() == null) {
            return ansi.fg(Ansi.Color.RED).a(it.getName().string()).reset().toString();
        }
        return it.getName().string() + " :" + it.getType().string();
    }

    @Override
    public String eval(ExpressionStatement statement) {
        return eval(statement.getStatement());
    }

    @Override
    public String eval(VariableStatement statement) {
        return "var " + statement.getDeclarations()
                .stream()
                .map(this::eval)
                .collect(Collectors.joining(","));
    }

    @Override
    public String eval(IfStatement statement) {
        var string = new StringBuilder().append("if ").append(eval(statement.getTest())).append("{\n").append(eval(statement.getConsequent())).append("\n}\n");
        if (statement.hasElse()) {
            string.append(" else {\n")
                    .append(eval(statement.getAlternate()))
                    .append("\n}\n");
        }
        return string.toString();
    }

    @Override
    public String eval(WhileStatement statement) {
        return "while (" + eval(statement.getTest()) + ") {\n"
                + eval(statement.getBody())
                + "\n}\n";
    }

    @Override
    public String eval(ForStatement statement) {
        return "";
    }

    @Override
    public String eval(SchemaDeclaration statement) {
        return "schema " + eval(statement.getName()) + " {\n" +
                eval(statement.getBody())
                + "\n}\n";
    }

    @Override
    public String eval(ReturnStatement statement) {
        return "return " + eval(statement.getArgument());
    }

    @Override
    public String eval(ResourceExpression expression) {
        return "resource " + eval(expression.getType()) + " " + eval(expression.getName()) + " {\n"
                + eval(expression.getBlock())
                + "}\n";
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
        return switch (expression) {
            case ParameterIdentifier parameterIdentifier -> formatParameter(parameterIdentifier);
            default -> expression.string();
        };
    }

    @Override
    public String eval(NullLiteral expression) {
        return "null";
    }

    @Override
    public String eval(StringLiteral expression) {
        return "\"" + expression.getValue() + "\"";
    }

    @Override
    public String eval(BlockExpression expression) {
        StringBuilder result = new StringBuilder();
        for (Statement statement : expression.getExpression()) {
            result.append("\t");
            result.append(eval(statement));
            result.append("\n");
        }
        return result.toString();
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