package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.visitors.LanguageAstPrinter;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Runtime.exceptions.NotFoundException;
import io.zmeu.types.Types;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TypeChecker implements Visitor<Types> {
    private final LanguageAstPrinter printer = new LanguageAstPrinter();
    @Getter
    private TypeEnvironment env;

    public TypeChecker() {
        env = new TypeEnvironment();
        env.init(Types.String.getValue(), Types.String);
        env.init(Types.Number.getValue(), Types.Number);
        env.init(Types.Boolean.getValue(), Types.Boolean);
        env.init(Types.Null.getValue(), Types.Null);
    }

    public TypeChecker(TypeEnvironment environment) {
        this.env = environment;
    }

    @Override
    public Types eval(Expression expression) {
        try {
            return expression.accept(this);
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }


    @Override
    public Types eval(Identifier expression) {
        try {
            var type = (Types) env.lookup(expression.getSymbol());
            return Objects.requireNonNullElseGet(type, () -> Types.valueOf(expression.getSymbol()));
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public Types eval(ResourceExpression expression) {
        return null;
    }

    @Override
    public Types eval(NullLiteral expression) {
        return Types.Null;
    }

    @Override
    public Types eval(StringLiteral expression) {
        return Types.String;
    }

    @Override
    public Types eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public Types eval(BlockExpression expression) {
        var env = new TypeEnvironment(this.env);
        return executeBlock(expression.getExpression(), env);
    }

    @NotNull
    Types executeBlock(List<Statement> statements, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            Types res = Types.Null;
            for (Statement statement : statements) {
                res = eval(statement);
            }
            return res;
        } finally {
            this.env = previous;
        }
    }

    Types executeBlock(Expression statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    Types executeBlock(Statement statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    @Override
    public Types eval(GroupExpression expression) {
        return null;
    }

    @Override
    public Types eval(BinaryExpression expression) {
        var op = expression.getOperator();
        Expression right = expression.getRight();
        Expression left = expression.getLeft();
        if (left == null || right == null) {
            throw new TypeError("Operator " + op + " expects 2 arguments");
        }
        var t1 = eval(left);
        var t2 = eval(right);

        var allowedTypes = allowTypes(op);
        this.expectOperatorType(t1, allowedTypes, expression);
        this.expectOperatorType(t2, allowedTypes, expression);
        return expect(t1, t2, left, expression);
    }

    private void expectOperatorType(Types type, List<Types> allowedTypes, BinaryExpression expression) {
        if (!allowedTypes.contains(type)) {
            throw new TypeError("Unexpected type: " + type + " in expression " + printer.eval(expression) + ". Allowed types: " + allowedTypes);
        }
    }

    private List<Types> allowTypes(String op) {
        return switch (op) {
            case "+", "==" -> List.of(Types.Number, Types.String); // allow addition for numbers and string
            case "-", "/", "*", "%" -> List.of(Types.Number);
            default -> throw new TypeError("Unknown operator " + op);
        };
    }

    private Types expect(Types actualType, Types expectedType, Expression expectedVal, Expression actualVal) {
        if (actualType == Types.Null) {
            return expectedType;
        }
        if (actualType != expectedType) {
            // only evaluate printing if we need to
            String string = "Expected type " + expectedType + " for value " + printer.eval(expectedVal) + " but got " + actualType + " in expression: " + printer.eval(actualVal);
            log.error(string);
            throw new TypeError(string);
        }
        return actualType;
    }

    @Override
    public Types eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public Types eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Types eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public Types eval(MemberExpression expression) {
        return null;
    }

    @Override
    public Types eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Types eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public Types eval(Program program) {
        return null;
    }

    @Override
    public Types eval(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public Types eval(InitStatement statement) {
        return null;
    }

    @Override
    public Types eval(FunctionDeclaration statement) {
        return null;
    }

    @Override
    public Types eval(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), env);
    }

    @Override
    public Types eval(VariableStatement statement) {
        var type = Types.Null;
        for (VariableDeclaration declaration : statement.getDeclarations()) {
            type = executeBlock(declaration, this.env);
        }
        return type;
    }

    @Override
    public Types eval(IfStatement statement) {
        return null;
    }

    @Override
    public Types eval(WhileStatement statement) {
        return null;
    }

    @Override
    public Types eval(ForStatement statement) {
        return null;
    }

    @Override
    public Types eval(SchemaDeclaration statement) {
        return null;
    }

    @Override
    public Types eval(ReturnStatement statement) {
        return null;
    }

    @Override
    public Types eval(VariableDeclaration expression) {
        var implicitType = eval(expression.getInit());
        String var = expression.getId().getSymbol();
        if (expression.hasType()) {
            var explicitType = eval(expression.getType());
            expect(implicitType, explicitType, expression.getInit(), expression);
            return (Types) env.init(var, explicitType);
        }
        return (Types) env.init(var, implicitType);
    }

    @Override
    public Types eval(AssignmentExpression expression) {
        return null;
    }

    @Override
    public Types eval(NumberLiteral expression) {
        return Types.Number;
    }

    @Override
    public Types eval(BooleanLiteral expression) {
        return Types.Boolean;
    }

    @Override
    public Types eval(float expression) {
        return Types.Number;
    }

    @Override
    public Types eval(double expression) {
        return Types.Number;
    }

    @Override
    public Types eval(int expression) {
        return Types.Number;
    }

    @Override
    public Types eval(boolean expression) {
        return Types.Boolean;
    }

    @Override
    public Types eval(String expression) {
        return Types.String;
    }
}
