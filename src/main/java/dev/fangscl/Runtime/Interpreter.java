package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Values.*;
import dev.fangscl.Runtime.exceptions.InvalidInitException;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import dev.fangscl.Runtime.exceptions.OperationNotImplementedException;
import dev.fangscl.Runtime.exceptions.RuntimeError;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class Interpreter implements
        dev.fangscl.Frontend.Parser.Expressions.Visitor<Object>,
        dev.fangscl.Frontend.Parser.Statements.Visitor<Object> {
    private static boolean hadRuntimeError;
    private Environment env;

    public Interpreter(Environment environment) {
        this.set(environment);
    }

    public Interpreter() {
        this(new Environment());
    }


    /**
     * Property access: instance.property.property
     */
    public <R> RuntimeValue<R> eval(MemberExpression expression) {
        if (expression.getProperty() instanceof Identifier resourceName) {
            var value = (IEnvironment) executeBlock(expression.getObject(), env);

            String symbol = resourceName.getSymbol();
            return value.lookup(symbol);
        }
        throw new OperationNotImplementedException("Membership expression not implemented for: " + expression.getKind());
    }

    @Override
    public Object eval(int expression) {
        return IntegerValue.of(expression);
    }

    @Override
    public Object eval(boolean expression) {
        return BooleanValue.of(expression);
    }

    @Override
    public Object eval(String expression) {
        return StringValue.of(expression);
    }

    @Override
    public Object eval(double expression) {
        return DecimalValue.of(expression);
    }

    public void set(Environment environment) {
        this.env = environment;
        this.env.init("null", NullValue.of());
        this.env.init("true", BooleanValue.of(true));
        this.env.init("false", BooleanValue.of(false));
    }

    @Override
    public Object eval(float expression) {
        return DecimalValue.of(expression);
    }

    @Override
    public Object eval(Expression expression) {
        return executeBlock(expression, env);
    }

    @Override
    public Object visit(NumericLiteral expression) {
        return switch (expression.getKind()) {
            case IntegerLiteral -> IntegerValue.of(expression);
            case DecimalLiteral -> DecimalValue.of(expression);
            default -> throw new IllegalStateException("Unexpected value: " + expression.getKind());
        };
    }

    @Override
    public Object visit(BooleanLiteral expression) {
        return BooleanValue.of(expression);
    }

    @Override
    public Object visit(Identifier expression) {
        return env.lookup(expression.getSymbol());
    }

    @Override
    public Object visit(NullLiteral expression) {
        return null;
    }

    @Override
    public Object eval(StringLiteral expression) {
        return StringValue.of(expression);
    }

    @Override
    public Object visit(LambdaExpression expression) {
        List<Expression> params = expression.getParams();
        Statement body = expression.getBody();
        return FunValue.of((Identifier) null, params, body, env);
    }

    @Override
    public Object visit(BlockStatement expression) {
        RuntimeValue res = NullValue.of();
        var env = new Environment(this.env);
        for (var it : expression.getExpression()) {
            res = (RuntimeValue) executeBlock(it, env);
        }
        return res;
    }

    @Override
    public Object visit(VariableStatement statement) {
        RuntimeValue res = NullValue.of();
        for (var it : statement.getDeclarations()) {
            res = (RuntimeValue) executeBlock(it, env);
        }
        return res;
    }

    @Override
    public Object visit(GroupExpression expression) {
        return null;
    }

    @Override
    public Object visit(BinaryExpression expression) {
        var lhs = executeBlock(expression.getLeft(), env);
        var rhs = executeBlock(expression.getRight(), env);
        if ((Object) expression.getOperator() instanceof String op) {
            if ((RuntimeValue) lhs instanceof IntegerValue lhsn && (RuntimeValue) rhs instanceof IntegerValue rhsn) {
                return switch (op) {
                    case "+" -> IntegerValue.of(lhsn.getValue() + rhsn.getValue());
                    case "-" -> IntegerValue.of(lhsn.getValue() - rhsn.getValue());
                    case "/" -> IntegerValue.of(lhsn.getValue() / rhsn.getValue());
                    case "*" -> IntegerValue.of(lhsn.getValue() * rhsn.getValue());
                    case "%" -> IntegerValue.of(lhsn.getValue() % rhsn.getValue());
                    case "==" -> BooleanValue.of(lhsn.getValue() == rhsn.getValue());
                    case "<" -> BooleanValue.of(lhsn.getValue() < rhsn.getValue());
                    case "<=" -> BooleanValue.of(lhsn.getValue() <= rhsn.getValue());
                    case ">" -> BooleanValue.of(lhsn.getValue() > rhsn.getValue());
                    case ">=" -> BooleanValue.of(lhsn.getValue() >= rhsn.getValue());
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            }
        }
        return null;
    }

    @Override
    public Object visit(CallExpression<Expression> expression) {
        RuntimeValue<Identifier> eval = (RuntimeValue<Identifier>) executeBlock(expression.getCallee(), env);
        FunValue function = (FunValue) eval;

        var args = expression.getArguments()
                .stream()
                .map(it -> (RuntimeValue<Object>) executeBlock(it, env))
                .toList();

        if (function.name() == null) { // execute lambda
            return lambdaCall(function, args);
        }

        return functionCall(function, args);
    }

    private <R> RuntimeValue<R> functionCall(FunValue function, List<RuntimeValue<Object>> args) {
        // for function execution, use the clojured environment from the declared scope
        var declared = (FunValue) function.getEnvironment()
                .lookup(function.name(), "Function not declared: " + function.name());


        var environment = new ActivationEnvironment(declared.getEnvironment(), declared.getParams(), args);
        RuntimeValue<R> res = (RuntimeValue<R>) executeBlock(declared.getBody(), environment);
        return res;
    }

    private <R> RuntimeValue<R> lambdaCall(FunValue function, List<RuntimeValue<Object>> args) {
        Environment activationEnvironment = new ActivationEnvironment(function.getEnvironment(), function.getParams(), args);
        return (RuntimeValue<R>) executeBlock(function.getBody(), activationEnvironment);
    }

    @Override
    public Object visit(ErrorExpression expression) {
        return null;
    }

    @Override
    public Object visit(LogicalExpression expression) {
        return null;
    }

    @Override
    public Object visit(MemberExpression expression) {
        if (expression.getProperty() instanceof Identifier resourceName) {
            var value = (IEnvironment) executeBlock(expression.getObject(), env);

            String symbol = resourceName.getSymbol();
            return value.lookup(symbol);
        }
        throw new OperationNotImplementedException("Membership expression not implemented for: " + expression.getObject());
    }


    @Override
    public Object visit(ResourceExpression expression) {
        if (expression.getName() == null) {
            throw new InvalidInitException("Resource does not have a name: " + expression.getType().getSymbol());
        }
        var schemaValueTmp = (RuntimeValue) executeBlock(expression.getType(), env);
        var schemaValue = (SchemaValue) schemaValueTmp;

        Environment schemaEnvironment = Optional.ofNullable(schemaValue.getEnvironment()).orElse(env);
        var resourceEnv = Environment.copyOf(schemaEnvironment);
        try {
            var args = new ArrayList<RuntimeValue<Object>>();
            for (Statement it : expression.getArguments()) {
                RuntimeValue<Object> objectRuntimeValue = (RuntimeValue<Object>) executeBlock(it, resourceEnv);
                args.add(objectRuntimeValue);
            }
            var init = schemaValue.getMethodOrNull("init");
            if (init != null) {
                functionCall(FunValue.of(init.name(), init.getParams(), init.getBody(), resourceEnv/* this env */), args);
            }
            return schemaEnvironment.init(expression.getName(), ResourceValue.of(expression.getName(), args, resourceEnv));
        } catch (NotFoundException e) {
            throw new NotFoundException("Field '%s' not found on resource '%s'".formatted(e.getObjectNotFound(), expression.name()));
        }
    }

    @Override
    public Object visit(ThisExpression expression) {
        return null;
    }

    @Override
    public Object visit(IfStatement statement) {
        RuntimeValue<Boolean> eval = (RuntimeValue<Boolean>) executeBlock(statement.getTest(), env);
        if (eval.getRuntimeValue()) {
            return executeBlock(statement.getConsequent(), env);
        } else {
            return executeBlock(statement.getAlternate(), env);
        }
    }

    @Override
    public Object visit(WhileStatement statement) {
        RuntimeValue<Object> result = NullValue.of();

        while (((Boolean) ((RuntimeValue<Object>) executeBlock(statement.getTest(), env)).getRuntimeValue())) {
            result = (RuntimeValue<Object>) executeBlock(statement.getBody(), env);
        }
        return result;
    }

    @Override
    public Object visit(ForStatement statement) {
        return null;
    }

    @Override
    public Object visit(SchemaDeclaration expression) {
        var name = expression.getName();
        var schemaEnv = new Environment(env);

        Statement body = expression.getBody();
        if (body instanceof ExpressionStatement statement && statement.getStatement() instanceof BlockStatement blockStatement) {
            executeBlock(blockStatement.getExpression(), schemaEnv); // install properties/methods of a schema into the environment
            return env.init(name.getSymbol(), SchemaValue.of(name, blockStatement, schemaEnv)); // install the schema into the global env
        }
        throw new RuntimeException("Invalid schema");
    }

    @Override
    public Object visit(UnaryExpression expression) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(1 + r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(1 + r.getRuntimeValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "--" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(r.getRuntimeValue() - 1);
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(
                                BigDecimal.valueOf(r.getRuntimeValue()).subtract(BigDecimal.ONE)
                                        .doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "-" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(-r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(BigDecimal.valueOf(r.getRuntimeValue()).negate().doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "!" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), env);
                    if (res instanceof BooleanValue r) {
                        yield BooleanValue.of(!r.isValue());
                    }
                    throw new RuntimeException("Invalid not operator: " + res.getRuntimeValue());
                }
                default -> throw new RuntimeException("Operator could not be evaluated: " + expression.getOperator());
            };
        }
        throw new RuntimeException("Operator could not be evaluated");
    }

    @Override
    public Object visit(VariableDeclaration expression) {
        String symbol = expression.getId().getSymbol();
        RuntimeValue value = null;
        if (expression.hasInit()) {
            value = (RuntimeValue) executeBlock(expression.getInit(), env);
        }
        return env.init(symbol, value);
    }

    @Override
    public Object visit(AssignmentExpression expression) {
        RuntimeValue right = (RuntimeValue) executeBlock(expression.getRight(), env);

        if (expression.getLeft() instanceof MemberExpression memberExpression) {
            var instanceEnv = (IEnvironment) executeBlock(memberExpression.getObject(), env);
            Expression property = memberExpression.getProperty();
            if (property instanceof Identifier identifier) {
                return instanceEnv.assign(identifier.getSymbol(), right);
            } else {
                throw new OperationNotImplementedException("Invalid assignment expression");
            }
        }

        RuntimeValue<String> left = IdentifierValue.of(expression.getLeft());
        return env.assign(left.getRuntimeValue(), right);
    }

    @Override
    public Object visit(Program program) {
        RuntimeValue lastEval = new NullValue();

        for (Statement i : program.getBody()) {
            lastEval = (RuntimeValue) executeBlock(i, env);
        }

        return lastEval;
    }

    @Override
    public Object eval(Statement statement) {
        return execute(statement);
    }

    @Override
    public Object visit(InitStatement statement) {
        var name = statement.getName();
        var params = statement.getParams();
        var body1 = statement.getBody();
        return executeBlock(FunctionDeclaration.of(name, params, body1), env);
    }

    @Override
    public Object visit(FunctionDeclaration declaration) {
        var name = declaration.getName();
        var params = declaration.getParams();
        var body = declaration.getBody();
        return env.init(name.getSymbol(), FunValue.of(name, params, body, env));
    }

    @Override
    public Object visit(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), env);
    }

    Object interpret(List<Statement> statements) {
        try {
            RuntimeValue res = null;
            for (Statement statement : statements) {
                res = (RuntimeValue) execute(statement);
            }
            return res;
        } catch (RuntimeError error) {
            runtimeError(error);
            return null;
        }
    }

    Object executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.env;
        try {
            this.env = environment;
            Object res = null;
            for (Statement statement : statements) {
                res = execute(statement);
            }
            return res;
        } finally {
            this.env = previous;
        }
    }

    Object executeBlock(Expression statement, Environment environment) {
        Environment previous = this.env;
        try {
            this.env = environment;
            return execute(statement);
        } finally {
            this.env = previous;
        }
    }

    Object executeBlock(Statement statement, Environment environment) {
        Environment previous = this.env;
        try {
            this.env = environment;
            return execute(statement);
        } finally {
            this.env = previous;
        }
    }

    private Object execute(Statement stmt) {
        return stmt.accept(this);
    }

    private Object execute(Expression stmt) {
        return stmt.accept(this);
    }

    static void runtimeError(RuntimeError error) {
        System.err.printf("%s\n[line %d]%n", error.getMessage(), error.getToken().getLine());
        hadRuntimeError = true;
    }
}
