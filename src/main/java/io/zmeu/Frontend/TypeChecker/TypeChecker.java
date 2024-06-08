package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.TypeChecker.Types.DataTypes;
import io.zmeu.Frontend.visitors.LanguageAstPrinter;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Runtime.exceptions.NotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TypeChecker implements Visitor<DataTypes> {
    private final LanguageAstPrinter printer = new LanguageAstPrinter();
    @Getter
    private TypeEnvironment env;

    public TypeChecker() {
        env = new TypeEnvironment();
        env.init(DataTypes.String.getValue(), DataTypes.String);
        env.init(DataTypes.Number.getValue(), DataTypes.Number);
        env.init(DataTypes.Boolean.getValue(), DataTypes.Boolean);
        env.init(DataTypes.Null.getValue(), DataTypes.Null);
    }

    public TypeChecker(TypeEnvironment environment) {
        this.env = environment;
    }

    @Override
    public DataTypes eval(Expression expression) {
        try {
            return expression.accept(this);
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }


    @Override
    public DataTypes eval(Identifier expression) {
        try {
            if (expression instanceof TypeIdentifier identifier) {
                var type = (DataTypes) env.lookup(identifier.getType().value());
                return Objects.requireNonNullElseGet(type, () -> DataTypes.valueOf(identifier.getType().value()));
            } else if (expression instanceof SymbolIdentifier identifier) {
                var type = (DataTypes) env.lookup(identifier.getSymbol());
                return Objects.requireNonNullElseGet(type, () -> DataTypes.valueOf(identifier.getSymbol()));
            }
            throw new TypeError(expression.string());
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public DataTypes eval(ResourceExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(NullLiteral expression) {
        return DataTypes.Null;
    }

    @Override
    public DataTypes eval(StringLiteral expression) {
        return DataTypes.String;
    }

    @Override
    public DataTypes eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(BlockExpression expression) {
        var env = new TypeEnvironment(this.env);
        return executeBlock(expression.getExpression(), env);
    }

    @NotNull
    private DataTypes executeBlock(List<Statement> statements, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            DataTypes res = DataTypes.Null;
            for (Statement statement : statements) {
                res = eval(statement);
            }
            return res;
        } finally {
            this.env = previous;
        }
    }

    private DataTypes executeBlock(Expression statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    private DataTypes executeBlock(Statement statement, TypeEnvironment environment) {
        TypeEnvironment previous = this.env;
        try {
            this.env = environment;
            return eval(statement);
        } finally {
            this.env = previous;
        }
    }

    @Override
    public DataTypes eval(GroupExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(BinaryExpression expression) {
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
        List<DataTypes> allowedTypes = allowTypes(op);
        this.expectOperatorType(t1, allowedTypes, expression);
        this.expectOperatorType(t2, allowedTypes, expression);

        if (isBooleanOp(op)) { // when is a boolean operation in an if statement, we return a boolean type(the result) else we return the type of the result for a + or *
            expect(t1, t2, left, expression);
            return DataTypes.Boolean;
        }
        return expect(t1, t2, left, expression);
    }

    private void expectOperatorType(DataTypes type, List<DataTypes> allowedTypes, BinaryExpression expression) {
        if (!allowedTypes.contains(type)) {
            throw new TypeError("Unexpected type: " + type + " in expression " + printer.eval(expression) + ". Allowed types: " + allowedTypes);
        }
    }

    private List<DataTypes> allowTypes(String op) {
        return switch (op) {
            case "+" -> List.of(DataTypes.Number, DataTypes.String); // allow addition for numbers and string
            case "-", "/", "*", "%" -> List.of(DataTypes.Number);
            case "==", "!=" -> List.of(DataTypes.String, DataTypes.Number, DataTypes.Boolean);
            case "<=", "<", ">", ">=" -> List.of(DataTypes.Number, DataTypes.Boolean);
            default -> throw new TypeError("Unknown operator " + op);
        };
    }

    private boolean isBooleanOp(String op) {
        return switch (op) {
            case "==", "<=", ">=", "!=", "<", ">" -> true;
            case null, default -> false;
        };
    }

    private DataTypes expect(DataTypes actualType, DataTypes expectedType, Expression expectedVal, Expression actualVal) {
        if (actualType == DataTypes.Null) {
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

    private DataTypes expect(DataTypes actualType, DataTypes expectedType, Statement expectedVal, Statement actualVal) {
        if (actualType == DataTypes.Null) {
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
    private DataTypes expect(DataTypes actualType, DataTypes expectedType, Expression expectedVal, Statement actualVal) {
        if (actualType == DataTypes.Null) {
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
    public DataTypes eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public DataTypes eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(MemberExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(ThisExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public DataTypes eval(Program program) {
        var type = DataTypes.Null;
        for (Statement statement : program.getBody()) {
            type = executeBlock(statement, env);
        }
        return type;
    }

    @Override
    public DataTypes eval(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public DataTypes eval(InitStatement statement) {
        return null;
    }

    @Override
    public DataTypes eval(FunctionDeclaration statement) {
        return null;
    }

    @Override
    public DataTypes eval(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), env);
    }

    @Override
    public DataTypes eval(VariableStatement statement) {
        var type = DataTypes.Null;
        for (VariableDeclaration declaration : statement.getDeclarations()) {
            type = executeBlock(declaration, this.env);
        }
        return type;
    }

    @Override
    public DataTypes eval(IfStatement statement) {
        DataTypes t1 = eval(statement.getTest());
        expect(t1, DataTypes.Boolean, statement.getTest(), statement.getTest());
        DataTypes t2 = eval(statement.getConsequent());
        DataTypes t3 = eval(statement.getAlternate());

        return expect(t3, t2, statement, statement);
    }

    @Override
    public DataTypes eval(WhileStatement statement) {
        var condition = eval(statement.getTest());
        expect(condition, DataTypes.Boolean, statement.getTest(), statement); // condition should always be boolean
        return eval(statement.getBody());
    }

    @Override
    public DataTypes eval(ForStatement statement) {
        return null;
    }

    @Override
    public DataTypes eval(SchemaDeclaration statement) {
        return null;
    }

    @Override
    public DataTypes eval(ReturnStatement statement) {
        return null;
    }

    @Override
    public DataTypes eval(VariableDeclaration expression) {
        var implicitType = eval(expression.getInit());
        String var = expression.getId().string();
        if (expression.hasType()) {
            var explicitType = eval(expression.getType());
            expect(implicitType, explicitType, expression.getInit(), expression);
            return (DataTypes) env.init(var, explicitType);
        }
        return (DataTypes) env.init(var, implicitType);
    }

    /**
     * Validates value assigned to x is of the same type as init type
     * var x = 1
     * x=2 // should allow number but not string
     */
    @Override
    public DataTypes eval(AssignmentExpression expression) {
        var varType = eval(expression.getLeft());
        var valueType = eval(expression.getRight());
        return expect(valueType, varType, expression.getLeft(), expression);
    }

    @Override
    public DataTypes eval(NumberLiteral expression) {
        return DataTypes.Number;
    }

    @Override
    public DataTypes eval(BooleanLiteral expression) {
        return DataTypes.Boolean;
    }

    @Override
    public DataTypes eval(float expression) {
        return DataTypes.Number;
    }

    @Override
    public DataTypes eval(double expression) {
        return DataTypes.Number;
    }

    @Override
    public DataTypes eval(int expression) {
        return DataTypes.Number;
    }

    @Override
    public DataTypes eval(boolean expression) {
        return DataTypes.Boolean;
    }

    @Override
    public DataTypes eval(String expression) {
        return DataTypes.String;
    }
}
