package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Values.*;
import dev.fangscl.Runtime.exceptions.OperationNotImplementedException;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Log4j2
public class Interpreter {
    private Environment global;

    public Interpreter(Environment global) {
        this.set(global);
    }

    public Interpreter() {
        this(new Environment());
    }

    public RuntimeValue eval(Program program, Environment environment) {
        RuntimeValue lastEval = new NullValue();

        for (Statement i : program.getBody()) {
            lastEval = eval(i, environment);
        }

        return lastEval;
    }

    public <R> RuntimeValue<R> eval(Statement expression) {
        return this.eval(expression, this.global);
    }

    public <R> RuntimeValue<R> eval(Statement statement, Environment env) {
        return switch (statement.getKind()) {
            case Program -> eval((Program) statement, env);
            case ExpressionStatement -> eval(((ExpressionStatement) statement).getStatement(), env);
            case IfStatement -> eval((IfStatement) statement, env);
            case WhileStatement -> eval((WhileStatement) statement, env);
            case VariableStatement -> eval((VariableStatement) statement, env);
            case FunctionDeclaration -> eval((FunctionDeclaration) statement, env);
            case SchemaDeclaration -> eval((SchemaDeclaration) statement, env);
            case InitDeclaration -> eval((InitStatement) statement, env);

            case ResourceExpression -> eval((ResourceExpression) statement, env);
            case CallExpression -> eval((CallExpression) statement, env);
            case LambdaExpression -> eval((LambdaExpression) statement, env);

            case StringLiteral -> StringValue.of(statement);
            case BooleanLiteral -> BooleanValue.of(statement);
            case IntegerLiteral -> IntegerValue.of(statement);
            case DecimalLiteral -> DecimalValue.of(statement);

            case BlockStatement -> eval((BlockStatement) statement, new Environment(env));
            case BinaryExpression -> eval((BinaryExpression) statement, env);
            case UnaryExpression -> eval((UnaryExpression) statement, env);
            case VariableDeclaration -> eval((VariableDeclaration) statement, env);
            case AssignmentExpression -> eval((AssignmentExpression) statement, env);
            case Identifier -> env.lookup(((Identifier) statement).getSymbol());
            default -> throw new OperationNotImplementedException(statement);
        };
    }

    public <R> RuntimeValue<R> eval(AssignmentExpression expression, Environment env) {
        RuntimeValue<String> left = IdentifierValue.of(expression.getLeft());
        RuntimeValue right = eval(expression.getRight(), env);
        return env.assign(left.getRuntimeValue(), right);
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
        var schemaEnvTmp = (RuntimeValue) eval(expression.getType(), env);
        var schemaEnv = (SchemaValue) schemaEnvTmp;
        var args = expression.getArguments()
                .stream()
                .map(it -> eval(it, env))
                .toList();

        var resourceEnv = new Environment(Optional.ofNullable(schemaEnv.getEnvironment()).orElse(env));
        var init = schemaEnv.getMethodOrNull("init");
        if (init != null) {
            return functionCall(FunValue.of(init.name(), init.getParams(), init.getBody(), resourceEnv), args);
        }
        return ResourceValue.of(expression.getName(), expression.getArguments(), resourceEnv);
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
        var declared = (FunValue) function.getEnvironment().lookup(function.name(), "Function not declared: ");

        Environment activationEnvironment = new ActivationEnvironment(declared.getEnvironment(), declared.getParams(), args);
        return evalBody(declared.getBody(), activationEnvironment);
    }

    private <R> RuntimeValue<R> lambdaCall(FunValue function, List<RuntimeValue<Object>> args) {
        Environment activationEnvironment = new ActivationEnvironment(function.getEnvironment(), function.getParams(), args);
        return eval(function.getBody(), activationEnvironment);
    }


    /**
     * Activation environment was already created so we don't need to create a new environment when we use a BlockStatement
     */
    public <R> RuntimeValue<R> evalBody(Statement statement, Environment env) {
        if (statement.is(NodeType.BlockStatement)) {
            return eval((BlockStatement) statement, env);
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

    public <R> RuntimeValue<R> eval(BlockStatement expression, Environment env) {
        RuntimeValue res = NullValue.of();
        for (var it : expression.getExpression()) {
            res = eval(it, env);
        }
        return res;
    }

    public RuntimeValue eval(VariableStatement statement, Environment env) {
        RuntimeValue res = NullValue.of();
        for (var it : statement.getDeclarations()) {
            res = eval(it, env);
        }
        return res;
    }

    public RuntimeValue eval(VariableDeclaration expression, Environment env) {
        String symbol = expression.getId().getSymbol();
        RuntimeValue value = null;
        if (expression.hasInit()) {
            value = eval(expression.getInit(), env);
        }
        return env.init(symbol, value);
    }

    public RuntimeValue<IntegerValue> eval(int expression) {
        return eval(NumericLiteral.of(expression));
    }

    public RuntimeValue eval(String expression) {
        return eval(StringLiteral.of(expression));
    }

    public RuntimeValue<Boolean> eval(boolean expression) {
        return eval(BooleanLiteral.of(expression));
    }

    public RuntimeValue<DecimalValue> eval(float value) {
        return eval(NumericLiteral.of(value));
    }

    public RuntimeValue<DecimalValue> eval(double expression) {
        return eval(NumericLiteral.of(expression));
    }

    private RuntimeValue eval(BinaryExpression expression, Environment environment) {
        var lhs = eval(expression.getLeft(), environment);
        var rhs = eval(expression.getRight(), environment);
        return eval(lhs, rhs, expression.getOperator());
    }

    private RuntimeValue eval(UnaryExpression expression, Environment env) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    RuntimeValue res = eval(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(1 + r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(1 + r.getRuntimeValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "--" -> {
                    RuntimeValue res = eval(expression.getValue(), env);
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
                    RuntimeValue res = eval(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(-r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(BigDecimal.valueOf(r.getRuntimeValue()).negate().doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res.getRuntimeValue());
                    }
                }
                case "!" -> {
                    RuntimeValue res = eval(expression.getValue(), env);
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

    private RuntimeValue eval(RuntimeValue lhs, RuntimeValue rhs, Object operator) {
        if (operator instanceof String op) {
            if (lhs instanceof IntegerValue lhsn && rhs instanceof IntegerValue rhsn) {
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

    public void set(Environment environment) {
        this.global = environment;
        this.global.init("null", NullValue.of());
        this.global.init("true", BooleanValue.of(true));
        this.global.init("false", BooleanValue.of(false));
    }
}
