package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
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
import java.util.List;
import java.util.Optional;

@Log4j2
public class Interpreter implements
        dev.fangscl.Frontend.Parser.Expressions.Visitor<Object>,
        dev.fangscl.Frontend.Parser.Statements.Visitor<Object> {
    private static boolean hadRuntimeError;
    private Environment environment;

    public Interpreter(Environment environment) {
        this.set(environment);
    }

    public Interpreter() {
        this(new Environment());
    }

    public <R> RuntimeValue<R> eval(Statement expression) {
        return this.eval(expression, this.environment);
    }

    public <R> RuntimeValue<R> eval(Statement statement, Environment env) {
        return switch (statement.getKind()) {
            case Program -> (RuntimeValue<R>) visit(statement);
            case ExpressionStatement -> (RuntimeValue<R>) visit(((ExpressionStatement) statement).getStatement());
            case IfStatement -> eval((IfStatement) statement, env);
            case WhileStatement -> eval((WhileStatement) statement, env);
            case VariableStatement -> (RuntimeValue<R>) visit((VariableStatement) statement);
            case FunctionDeclaration -> eval((FunctionDeclaration) statement, env);
            case SchemaDeclaration -> eval((SchemaDeclaration) statement, env);
            case InitDeclaration -> eval((InitStatement) statement, env);
            case MemberExpression -> eval((MemberExpression) statement, env);

            case ResourceExpression -> eval((ResourceExpression) statement, env);
            case CallExpression -> eval((CallExpression) statement, env);
            case LambdaExpression -> eval((LambdaExpression) statement, env);

            case StringLiteral -> (RuntimeValue<R>) visit(statement);
            case BooleanLiteral -> BooleanValue.of(statement);
            case IntegerLiteral -> IntegerValue.of(statement);
            case DecimalLiteral -> DecimalValue.of(statement);

            case BlockStatement ->(RuntimeValue<R>) visit((BlockStatement) statement);
            case BinaryExpression -> (RuntimeValue<R>) visit(statement);
            case UnaryExpression -> (RuntimeValue<R>) visit((UnaryExpression) statement);
            case VariableDeclaration -> (RuntimeValue<R>) visit((VariableDeclaration) statement);
            case AssignmentExpression -> (RuntimeValue<R>) visit(statement);
            case Identifier -> (RuntimeValue<R>) visit((Identifier) statement);
            default -> throw new OperationNotImplementedException(statement);
        };
    }

    public <R> RuntimeValue<R> eval(FunctionDeclaration expression, Environment env) {
        var name = expression.getName();
        var params = expression.getParams();
        var body = expression.getBody();
        return env.init(name.getSymbol(), FunValue.of(name, params, body, env));
    }

    public <R> RuntimeValue<R> eval(SchemaDeclaration expression, Environment env) {
        var name = expression.getName();
        var schemaEnv = new Environment(env);

        evalBody(expression.getBody(), schemaEnv); // install properties/methods of a schema into the environment
        return env.init(name.getSymbol(), SchemaValue.of(name, (BlockStatement) expression.getBody(), schemaEnv)); // install the schema into the global env
    }

    /**
     * InitStatement
     * Syntactic sugar for a function
     */
    public <R> RuntimeValue<R> eval(InitStatement expression, Environment env) {
        var name = expression.getName();
        var params = expression.getParams();
        var body1 = expression.getBody();
        return eval(FunctionDeclaration.of(name, params, body1), env);
    }

    /**
     * Property access: instance.property.property
     */
    public <R> RuntimeValue<R> eval(MemberExpression expression, Environment env) {
        if (expression.getProperty() instanceof Identifier resourceName) {
            var value = (IEnvironment) eval(expression.getObject(), env);

            String symbol = resourceName.getSymbol();
            return value.lookup(symbol);
        }
        throw new OperationNotImplementedException("Membership expression not implemented for: " + expression.getKind());
    }

    public RuntimeValue eval(LambdaExpression expression, Environment env) {
        List<Expression> params = expression.getParams();
        Statement body = expression.getBody();
        return FunValue.of((Identifier) null, params, body, env);
    }

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    public RuntimeValue eval(ResourceExpression expression, Environment env) {
        if (expression.getName() == null) {
            throw new InvalidInitException("Resource does not have a name: " + expression.getType().getSymbol());
        }
        var schemaValueTmp = (RuntimeValue) eval(expression.getType(), env);
        var schemaValue = (SchemaValue) schemaValueTmp;

        Environment schemaEnvironment = Optional.ofNullable(schemaValue.getEnvironment()).orElse(env);
        var resourceEnv = Environment.copyOf(schemaEnvironment);
        try {
            var args = expression.getArguments()
                    .stream()
                    .map(it -> eval(it, resourceEnv))
                    .toList();
            var init = schemaValue.getMethodOrNull("init");
            if (init != null) {
                functionCall(FunValue.of(init.name(), init.getParams(), init.getBody(), resourceEnv/* this env */), args);
            }
            return schemaEnvironment.init(expression.getName(), ResourceValue.of(expression.getName(), args, resourceEnv));
        } catch (NotFoundException e) {
            throw new NotFoundException("Field '%s' not found on resource '%s'".formatted(e.getObjectNotFound(), expression.name()));
        }

    }

    public <R> RuntimeValue<R> eval(CallExpression<Expression> expression, Environment env) {
        RuntimeValue<Identifier> eval = eval(expression.getCallee(), env);
        FunValue function = (FunValue) eval;

        var args = expression.getArguments()
                .stream()
                .map(it -> eval(it, env))
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
        RuntimeValue<R> res = evalBody(declared.getBody(), environment);
        return res;
    }

    private <R> RuntimeValue<R> lambdaCall(FunValue function, List<RuntimeValue<Object>> args) {
        Environment activationEnvironment = new ActivationEnvironment(function.getEnvironment(), function.getParams(), args);
        return eval(function.getBody(), activationEnvironment);
    }


    /**
     * Activation environment was already created so we don't need to create a new environment when we use a BlockStatement
     */
    public <R> RuntimeValue<R> evalBody(Statement statement, Environment env) {
        if (statement instanceof BlockStatement blockStatement) {
            return eval(blockStatement, env);
        }
        return eval(statement, env);
    }

    public <R> RuntimeValue<R> eval(IfStatement statement, Environment env) {
        RuntimeValue<Boolean> eval = eval(statement.getTest(), env);
        if (eval.getRuntimeValue()) {
            return eval(statement.getConsequent(), env);
        } else {
            return eval(statement.getAlternate(), env);
        }
    }

    public RuntimeValue eval(WhileStatement statement, Environment env) {
        RuntimeValue<Object> result = NullValue.of();

        while ((Boolean) eval(statement.getTest(), env).getRuntimeValue()) {
            result = eval(statement.getBody(), env);
        }
        return result;
    }

    public RuntimeValue eval(String expression) {
        return (RuntimeValue) visit(expression);
    }

    public RuntimeValue<Boolean> eval(boolean expression) {
        return (RuntimeValue<Boolean>) visit(expression);
    }

    public RuntimeValue<DecimalValue> eval(float value) {
        return (RuntimeValue<DecimalValue>) visit(value);
    }

    @Override
    public Object visit(int expression) {
        return IntegerValue.of(expression);
    }

    @Override
    public Object visit(boolean expression) {
        return BooleanValue.of(expression);
    }

    @Override
    public Object visit(String expression) {
        return StringValue.of(expression);
    }

    @Override
    public Object visit(double expression) {
        return DecimalValue.of(expression);
    }

    public RuntimeValue<DecimalValue> eval(double expression) {
        return (RuntimeValue<DecimalValue>) visit(expression);
    }

    public void set(Environment environment) {
        this.environment = environment;
        this.environment.init("null", NullValue.of());
        this.environment.init("true", BooleanValue.of(true));
        this.environment.init("false", BooleanValue.of(false));
    }

    @Override
    public Object visit(float expression) {
        return DecimalValue.of(expression);
    }

    @Override
    public Object visit(Expression expression) {
        return null;
    }

    @Override
    public Object visit(NumericLiteral expression) {
        return IntegerValue.of(expression);
    }

    @Override
    public Object visit(BooleanLiteral expression) {
        return BooleanValue.of(expression);
    }

    @Override
    public Object visit(Identifier expression) {
        return environment.lookup(expression.getSymbol());
    }

    @Override
    public Object visit(NullLiteral expression) {
        return null;
    }

    @Override
    public Object visit(StringLiteral expression) {
        return null;
    }

    @Override
    public Object visit(LambdaExpression expression) {
        return null;
    }

    @Override
    public Object visit(BlockStatement expression) {
        RuntimeValue res = NullValue.of();
        var env = new Environment(environment);
        for (var it : expression.getExpression()) {
            if (it instanceof BlockStatement blockStatement) {
                res = (RuntimeValue) executeBlock(blockStatement.getExpression(), environment);
            }else {
                res = (RuntimeValue) executeBlock(it, env);
            }
        }
        return res;
    }

    @Override
    public Object visit(VariableStatement statement) {
        RuntimeValue res = NullValue.of();
        for (var it : statement.getDeclarations()) {
            res = (RuntimeValue) executeBlock(it, environment);
        }
        return res;
    }

    @Override
    public Object visit(GroupExpression expression) {
        return null;
    }

    @Override
    public Object visit(BinaryExpression expression) {
        var lhs = eval(expression.getLeft(), environment);
        var rhs = eval(expression.getRight(), environment);
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
    public Object visit(CallExpression expression) {
        return null;
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
        return null;
    }

    @Override
    public Object visit(ResourceExpression expression) {
        return null;
    }

    @Override
    public Object visit(ThisExpression expression) {
        return null;
    }

    @Override
    public Object visit(UnaryExpression expression) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), environment);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(1 + r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(1 + r.getRuntimeValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "--" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), environment);
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
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), environment);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(-r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(BigDecimal.valueOf(r.getRuntimeValue()).negate().doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "!" -> {
                    RuntimeValue res = (RuntimeValue) executeBlock(expression.getValue(), environment);
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
//        var env = new Environment(environment);
        if (expression.hasInit()) {
            value = (RuntimeValue) executeBlock(expression.getInit(), environment);
        }
        return environment.init(symbol, value);
    }

    @Override
    public Object visit(AssignmentExpression expression) {
        RuntimeValue right = eval(expression.getRight(), environment);

        if (expression.getLeft() instanceof MemberExpression memberExpression) {
            var instanceEnv = (IEnvironment) eval(memberExpression.getObject(), environment);
            Expression property = memberExpression.getProperty();
            if (property instanceof Identifier identifier) {
                return instanceEnv.assign(identifier.getSymbol(), right);
            } else {
                throw new OperationNotImplementedException("Invalid assignment expression");
            }
        }

        RuntimeValue<String> left = IdentifierValue.of(expression.getLeft());
        return environment.assign(left.getRuntimeValue(), right);
    }

    @Override
    public Object visit(Program program) {
        RuntimeValue lastEval = new NullValue();

        for (Statement i : program.getBody()) {
            lastEval = evalBody(i, environment);
        }

        return lastEval;
    }

    @Override
    public Object visit(Statement statement) {
        return execute(statement);
    }

    @Override
    public Object visit(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), environment);
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
        Environment previous = this.environment;
        try {
            this.environment = environment;
            Object res = null;
            for (Statement statement : statements) {
                res = execute(statement);
            }
            return res;
        } finally {
            this.environment = previous;
        }
    }

    Object executeBlock(Expression statement, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            return execute(statement);
        } finally {
            this.environment = previous;
        }
    }

    Object executeBlock(Statement statement, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            return execute(statement);
        } finally {
            this.environment = previous;
        }
    }

    private Object execute(Statement stmt) {
        return stmt.accept(this);
    }

    private Object execute(Expression stmt) {
        return stmt.accept((Visitor<Object>) this);
    }

    static void runtimeError(RuntimeError error) {
        System.err.printf("%s\n[line %d]%n", error.getMessage(), error.getToken().getLine());
        hadRuntimeError = true;
    }
}
