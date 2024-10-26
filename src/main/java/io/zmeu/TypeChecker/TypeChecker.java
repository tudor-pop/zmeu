package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Runtime.exceptions.NotFoundException;
import io.zmeu.TypeChecker.Types.*;
import io.zmeu.Visitors.LanguageAstPrinter;
import io.zmeu.Visitors.Visitor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
public final class TypeChecker implements Visitor<Type> {
    private final LanguageAstPrinter printer = new LanguageAstPrinter();
    @Getter
    private TypeEnvironment env;

    public TypeChecker() {
        env = new TypeEnvironment();
        env.init(ValueType.String.getValue(), ValueType.String);
        env.init(ValueType.Number.getValue(), ValueType.Number);
        env.init(ValueType.Boolean.getValue(), ValueType.Boolean);
        env.init(ValueType.Void.getValue(), ValueType.Void);
        env.init(ValueType.Null.getValue(), ValueType.Null);
        env.init("pow", TypeFactory.fromString("(Number,Number)->Number"));
        env.init("string", TypeFactory.fromString("(Number)->String"));
    }

    public TypeChecker(TypeEnvironment environment) {
        this.env = environment;
    }

    @Override
    public Type eval(Expression expression) {
        try {
            return expression.accept(this);
        } catch (NotFoundException | TypeError exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }


    @Override
    public Type eval(Identifier expression) {
        try {
            if (expression instanceof TypeIdentifier identifier) {
                Type type = env.lookup(identifier.getType().getValue());
                return Objects.requireNonNullElseGet(type, () -> TypeFactory.fromString(identifier.getType().getValue()));
            } else if (expression instanceof SymbolIdentifier identifier) {
                Type type = env.lookup(identifier.getSymbol());
                return Objects.requireNonNullElseGet(type, () -> TypeFactory.fromString(identifier.getSymbol()));
            }
            throw new TypeError(expression.string());
        } catch (NotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
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
            expect(t1, t2, left);
            return ValueType.Boolean;
        }
        return expect(t1, t2, left);
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

    private Type expect(Type actualType, Type expectedType, Expression expectedVal) {
//        if (actualType == ValueType.Null) {
//            return expectedType;
//        }
        if (expectedType == ValueType.Null) {
            return actualType;
        }
        if (!Objects.equals(actualType, expectedType)) {
            // only evaluate printing if we need to
            String string = "Expected type " + expectedType + " but got " + actualType + " in expression: " + printer.eval(expectedVal);
            throw new TypeError(string);
        }
        return actualType;
    }

    private Type expect(Type actualType, Type expectedType, Statement actualVal, Statement expectedVal) {
        if (actualType == null || actualType == ValueType.Null) {
            return expectedType;
        }
        if (expectedType == ValueType.Null) {
            return actualType;
        }
        if (!Objects.equals(actualType, expectedType)) {
            // only evaluate printing if we need to
            String string = "Expected type " + expectedType + " for value " + printer.eval(expectedVal) + " but got " + actualType + " in expression: " + printer.eval(actualVal);
            throw new TypeError(string);
        }
        return actualType;
    }

    private Type expect(Type actualType, Type expectedType, Statement actualVal, Expression expectedVal) {
        if (actualType == ValueType.Null) {
            return expectedType;
        }
        if (expectedType == ValueType.Null) {
            return actualType;
        }
        if (!Objects.equals(actualType, expectedType)) {
            // only evaluate printing if we need to
            String string = "Expected type " + expectedType + " for value " + printer.eval(expectedVal) + " but got " + actualType + " in expression: " + printer.eval(actualVal);
            throw new TypeError(string);
        }
        return actualType;
    }

    @Override
    public Type eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Type eval(LogicalExpression expression) {
        Type left = eval(expression.getLeft());
        Type right = eval(expression.getRight());
        expect(left, ValueType.Boolean, expression);
        expect(right, ValueType.Boolean, expression);
        return expect(left, right, expression);
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
        var operator = expression.getOperator();
        return switch (operator) {
            case "++", "--", "-" -> executeBlock(expression.getValue(), env);
            case "!" -> {
                var res = executeBlock(expression.getValue(), env);
                if (res == ValueType.Boolean) {
                    yield res;
                }
                throw new RuntimeException("Invalid not operator: " + res);
            }
            default -> throw new RuntimeException("Operator could not be evaluated: " + expression.getOperator());
        };
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
    public Type eval(Type type) {
        return type;
    }

    @Override
    public Type eval(InitStatement statement) {
        return null;
    }

    @Override
    public Type eval(FunctionDeclaration expression) {
        var params = convertParams(expression.getParams());
        var funType = new FunType(params.values(), expression.getReturnType().getType());
        env.init(expression.getName(), funType); // save function signature in env so that we're able to call it later to validate types

        var actualReturn = validateBody(expression.getReturnType().getType(), expression.getBody(), params);
        funType.setReturnType(actualReturn);
        return funType;
    }

    @Override
    public Type eval(LambdaExpression expression) {
        var params = convertParams(expression.getParams());
        // if return type missing from parsing because Void was not specified then convert it to the actual return type from the body
        TypeIdentifier returnType = Optional.ofNullable(expression.getReturnType())
                .orElse(TypeIdentifier.type(ValueType.Null));

        var funType = new FunType(params.values(), eval(returnType));
        var actualReturn = validateBody(returnType.getType(), expression.getBody(), params);
        funType.setReturnType(actualReturn);
        return funType;
    }

    private @NotNull Map<String, Type> convertParams(List<ParameterIdentifier> params) {
        var collect = new HashMap<String, Type>(params.size());
        for (ParameterIdentifier identifier : params) {
            if (identifier.getType() == null) {
                throw new IllegalArgumentException("Missing type for parameter " + identifier.getName().string() + "(" + printer.eval(identifier) + ")");
            }
            Type type = identifier.getType().getType();
            if (collect.put(identifier.getName().getSymbol(), type) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return collect;
    }

    private @NotNull Type validateBody(@Nullable Type returnType, Statement body, Map<String, Type> collect) {
        var funEnv = new TypeEnvironment(env, collect);
        var actualReturnType = executeBlock(body, funEnv);

        return expect(actualReturnType, returnType, body, returnType);
    }

    @Override
    public Type eval(CallExpression<Expression> expression) {
        FunType fun = (FunType) eval(expression.getCallee()); // extract the function type itself

        var passedArgumentsTypes = expression.getArguments()
                .stream()
                .map(this::eval)
                .toList();
        checkFunctionCall(fun, passedArgumentsTypes, expression);
        return fun.getReturnType();
    }

    private void checkFunctionCall(FunType fun, List<Type> args, CallExpression<Expression> expression) {
        if (fun.getParams().size() != args.size()) {
            String string = "Function '" + printer.eval(expression.getCallee()) + "' expects " + fun.getParams().size() + " arguments but got " + args.size() + " in " + printer.eval(expression);
            throw new TypeError(string);
        }
        for (int i = 0; i < args.size(); i++) {
            try {
                var param = fun.getParams().get(i);
                Type actual = args.get(i);
                expect(actual, param, expression);
            } catch (IndexOutOfBoundsException exception) {
            }
        }
    }

    @Override
    public Type eval(ReturnStatement statement) {
        return eval(statement.getArgument());
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
        expect(t1, ValueType.Boolean, statement.getTest());
        Type t2 = eval(statement.getConsequent());
        Type t3 = null;
        if (statement.getAlternate() != null) {
            t3 = eval(statement.getAlternate());
        }

        return expect(t3, t2, statement, statement);
    }

    @Override
    public Type eval(WhileStatement statement) {
        var condition = eval(statement.getTest());
        expect(condition, ValueType.Boolean, statement, statement.getTest()); // condition should always be boolean
        return eval(statement.getBody());
    }

    @Override
    public Type eval(ForStatement statement) {
        List<Statement> statements = statement.discardBlock();
        statements.add(ExpressionStatement.expressionStatement(statement.getUpdate()));
        var whileStatement = WhileStatement.of(statement.getTest(), BlockExpression.block(statements));
        if (statement.getInit() == null) {
            return executeBlock(whileStatement, env);
        }
        return executeBlock(BlockExpression.block(statement.getInit(), whileStatement), env);
    }

    @Override
    public Type eval(SchemaDeclaration schema) {
        var name = schema.getName();
        var body = schema.getBody();

        var schemaType = new SchemaType(name.string(), env);
        env.init(name, schemaType);

        if (schema.getBody() instanceof ExpressionStatement statement && statement.getStatement() instanceof BlockExpression blockExpression) {
            executeBlock(blockExpression.getExpression(), schemaType.getEnvironment());
            return schemaType;
        }
        throw new RuntimeException("Invalid schema declaration: " + schema.getName());
    }

    @Override
    public Type eval(ResourceExpression expression) {
        return null;
    }

    @Override
    public Type eval(VariableDeclaration expression) {
        String var = expression.getId().string();
        if (expression.getInit() != null) {
            var implicitType = eval(expression.getInit());
            if (expression.hasType()) {
                var explicitType = eval(expression.getType());
                expect(implicitType, explicitType, expression);
                return env.init(var, explicitType);
            }
            return env.init(var, implicitType);
        } else if (expression.hasType()) {
            var explicitType = eval(expression.getType());
            return env.init(var, explicitType);
        } else {
            throw new IllegalArgumentException("Missing explicit and implicit type for variable " + var);
        }
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
        return expect(valueType, varType, expression.getLeft());
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
