package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.Types.Type;
import io.zmeu.Frontend.Parser.Types.ValueType;
import io.zmeu.Frontend.visitors.LanguageAstPrinter;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Runtime.exceptions.NotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TypeChecker implements Visitor<Type> {
    private final LanguageAstPrinter printer = new LanguageAstPrinter();
    @Getter
    private TypeEnvironment env;

    public TypeChecker() {
        env = new TypeEnvironment();
        env.init(ValueType.String.getValue(), ValueType.String);
        env.init(ValueType.Number.getValue(), ValueType.Number);
        env.init(ValueType.Boolean.getValue(), ValueType.Boolean);
        env.init(ValueType.Null.getValue(), ValueType.Null);
    }

    public TypeChecker(TypeEnvironment environment) {
        this.env = environment;
    }

    @Override
    public Type eval(Expression expression) {
        try {
            return expression.accept(this);
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }


    @Override
    public Type eval(Identifier expression) {
        try {
            if (expression instanceof TypeIdentifier identifier) {
                var type = (Type) env.lookup(identifier.getType().getValue());
                return Objects.requireNonNullElseGet(type, () -> Type.valueOf(identifier.getType().getValue()));
            } else if (expression instanceof SymbolIdentifier identifier) {
                var type = (Type) env.lookup(identifier.getSymbol());
                return Objects.requireNonNullElseGet(type, () -> Type.valueOf(identifier.getSymbol()));
            }
            throw new TypeError(expression.string());
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public Type eval(ResourceExpression expression) {
        return null;
    }

    @Override
    public Type eval(NullLiteral expression) {
        return ValueType.Null;
    }

    @Override
    public Type eval(StringLiteral expression) {
        return ValueType.String;
    }

    @Override
    public Type eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public Type eval(BlockExpression expression) {
        var env = new TypeEnvironment(this.env);
        return executeBlock(expression.getExpression(), env);
    }

    @NotNull
    private Type executeBlock(List<Statement> statements, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            Type res = ValueType.Null;
            for (Statement statement : statements) {
                res = eval(statement);
            }
            return res;
        } finally {
            this.env = previous;
        }
    }

    private Type executeBlock(Expression statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    private Type executeBlock(Statement statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    @Override
    public Type eval(GroupExpression expression) {
        return null;
    }

    @Override
    public Type eval(BinaryExpression expression) {
        var op = expression.getOperator();
        Expression right = expression.getRight();
        Expression left = expression.getLeft();
        if (left == null || right == null) {
            throw new TypeError("Operator " + op + " expects 2 arguments");
        }
        var t1 = eval(left);
        var t2 = eval(right);

        // allow operations only on the same types of values
        // 1+1, "hello "+"world", 1/2, 1<2, "hi" == "hi"
        List<Type> allowedTypes = allowTypes(op);
        this.expectOperatorType(t1, allowedTypes, expression);
        this.expectOperatorType(t2, allowedTypes, expression);

        if (isBooleanOp(op)) { // when is a boolean operation in an if statement, we return a boolean type(the result) else we return the type of the result for a + or *
            expect(t1, t2, left, expression);
            return ValueType.Boolean;
        }
        return expect(t1, t2, left, expression);
    }

    private void expectOperatorType(Type type, List<Type> allowedTypes, BinaryExpression expression) {
        if (!allowedTypes.contains(type)) {
            throw new TypeError("Unexpected type: " + type + " in expression " + printer.eval(expression) + ". Allowed types: " + allowedTypes);
        }
    }

    private List<Type> allowTypes(String op) {
        return switch (op) {
            case "+" -> List.of(ValueType.Number, ValueType.String); // allow addition for numbers and string
            case "-", "/", "*", "%" -> List.of(ValueType.Number);
            case "==", "!=" -> List.of(ValueType.String, ValueType.Number, ValueType.Boolean);
            case "<=", "<", ">", ">=" -> List.of(ValueType.Number, ValueType.Boolean);
            default -> throw new TypeError("Unknown operator " + op);
        };
    }

    private boolean isBooleanOp(String op) {
        return switch (op) {
            case "==", "<=", ">=", "!=", "<", ">" -> true;
            case null, default -> false;
        };
    }

    private Type expect(Type actualType, Type expectedType, Expression expectedVal, Expression actualVal) {
        if (actualType == ValueType.Null) {
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

    private Type expect(Type actualType, Type expectedType, Statement expectedVal, Statement actualVal) {
        if (actualType == ValueType.Null) {
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
    private Type expect(Type actualType, Type expectedType, Expression expectedVal, Statement actualVal) {
        if (actualType == ValueType.Null) {
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
    public Type eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public Type eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Type eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public Type eval(MemberExpression expression) {
        return null;
    }

    @Override
    public Type eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Type eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public Type eval(Program program) {
        Type type = ValueType.Null;
        for (Statement statement : program.getBody()) {
            type = executeBlock(statement, env);
        }
        return type;
    }

    @Override
    public Type eval(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public Type eval(InitStatement statement) {
        return null;
    }

    @Override
    public Type eval(FunctionDeclaration statement) {


        return null;
    }

    @Override
    public Type eval(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), env);
    }

    @Override
    public Type eval(VariableStatement statement) {
        Type type = ValueType.Null;
        for (VariableDeclaration declaration : statement.getDeclarations()) {
            type = executeBlock(declaration, this.env);
        }
        return type;
    }

    @Override
    public Type eval(IfStatement statement) {
        Type t1 = eval(statement.getTest());
        expect(t1, ValueType.Boolean, statement.getTest(), statement.getTest());
        Type t2 = eval(statement.getConsequent());
        Type t3 = eval(statement.getAlternate());

        return expect(t3, t2, statement, statement);
    }

    @Override
    public Type eval(WhileStatement statement) {
        var condition = eval(statement.getTest());
        expect(condition, ValueType.Boolean, statement.getTest(), statement); // condition should always be boolean
        return eval(statement.getBody());
    }

    @Override
    public Type eval(ForStatement statement) {
        return null;
    }

    @Override
    public Type eval(SchemaDeclaration statement) {
        return null;
    }

    @Override
    public Type eval(ReturnStatement statement) {
        return null;
    }

    @Override
    public Type eval(VariableDeclaration expression) {
        var implicitType = eval(expression.getInit());
        String var = expression.getId().string();
        if (expression.hasType()) {
            var explicitType = eval(expression.getType());
            expect(implicitType, explicitType, expression.getInit(), expression);
            return (Type) env.init(var, explicitType);
        }
        return (Type) env.init(var, implicitType);
    }

    /**
     * Validates value assigned to x is of the same type as init type
     * var x = 1
     * x=2 // should allow number but not string
     */
    @Override
    public Type eval(AssignmentExpression expression) {
        var varType = eval(expression.getLeft());
        var valueType = eval(expression.getRight());
        return expect(valueType, varType, expression.getLeft(), expression);
    }

    @Override
    public Type eval(NumberLiteral expression) {
        return ValueType.Number;
    }

    @Override
    public Type eval(BooleanLiteral expression) {
        return ValueType.Boolean;
    }

    @Override
    public Type eval(float expression) {
        return ValueType.Number;
    }

    @Override
    public Type eval(double expression) {
        return ValueType.Number;
    }

    @Override
    public Type eval(int expression) {
        return ValueType.Number;
    }

    @Override
    public Type eval(boolean expression) {
        return ValueType.Boolean;
    }

    @Override
    public Type eval(String expression) {
        return ValueType.String;
    }
}
