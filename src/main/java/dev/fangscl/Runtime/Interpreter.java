package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Values.*;
import dev.fangscl.Runtime.exceptions.OperationNotImplementedException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

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
            case FunctionDeclarationStatement -> eval((FunctionDeclarationStatement) statement, env);

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

    public <R> RuntimeValue<R> eval(FunctionDeclarationStatement expression, Environment env) {
        var name = expression.getName();
        var params = expression.getParams();
        var body = expression.getBody();
        return env.init(name.getSymbol(), FunValue.of(name, params, body, env));
    }

    public RuntimeValue eval(LambdaExpression expression, Environment env) {
        var args = expression.getParams();

        return FunValue.of(null, args, expression.getBody(), env);
    }

    public <R> RuntimeValue<R> eval(CallExpression<Expression> expression, Environment env) {
        RuntimeValue function = eval(expression.getCallee(), env);
        FunValue funValue = (FunValue) function;
        var args = expression.getArguments()
                .stream()
                .map(it -> eval(it, env))
                .toList();

        if (funValue.getName() == null) { // execute lambda
            Environment activationRecord = getActivationRecord(args, funValue.getEnvironment(), funValue.getParams());
            return eval(funValue.getBody(), activationRecord);
        }

        String symbol = funValue.getName().getSymbol();
        var declared = (FunValue) funValue.getEnvironment().lookup(symbol);
        if (declared == null) {
            throw new RuntimeException("Function not declared: " + symbol);
        }

        // for function execution, use the clojured environment from the declared scope
        Environment activationRecord = getActivationRecord(args, declared.getEnvironment(), declared.getParams());
        return eval(declared.getBody(), activationRecord);
    }

    @NotNull
    private Environment getActivationRecord(List<RuntimeValue<Object>> args, Environment environment, List<Expression> params) {
        var activationRecord = new Environment(environment);
        for (var i = 0; i < params.size(); i++) {
            // for each named parameter, we save the argument into the activation record(env that the function uses to execute)
            var paramName = ((Identifier) params.get(i)).getSymbol();
            activationRecord.init(paramName, args.get(i));
        }
        return activationRecord;
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
        RuntimeValue res = new NullValue();
        for (var it : expression.getExpression()) {
            res = eval(it, env);
        }
        return res;
    }

    public RuntimeValue eval(VariableDeclaration expression, Environment env) {
        String symbol = expression.getId().getSymbol();
        RuntimeValue value = eval(expression.getInit(), env);
        return env.init(symbol, value);
    }

    public RuntimeValue eval(VariableStatement statement, Environment env) {
        RuntimeValue res = new NullValue();
        for (var it : statement.getDeclarations()) {
            res = eval(it, env);
        }
        return res;
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
        this.global.init("null", new NullValue());
        this.global.init("true", BooleanValue.of(true));
        this.global.init("false", BooleanValue.of(false));
    }
}
