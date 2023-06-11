package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Functions.DateFunction;
import dev.fangscl.Runtime.Functions.Numeric.IntCastFunction;
import dev.fangscl.Runtime.Functions.Numeric.MaxFunction;
import dev.fangscl.Runtime.Functions.Numeric.MinFunction;
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
        Visitor<Object>,
        dev.fangscl.Frontend.Parser.Statements.Visitor<Object> {
    private static boolean hadRuntimeError;
    private Environment env;

    public Interpreter(Environment environment) {
        this.set(environment);
    }

    public Interpreter() {
        this(new Environment());
    }

    public void set(Environment environment) {
        this.env = environment;
        this.env.init("null", NullValue.of());
        this.env.init("true", BooleanValue.of(true));
        this.env.init("false", BooleanValue.of(false));
        this.env.init("int", new IntCastFunction());
        this.env.init("min", new MinFunction());
        this.env.init("max", new MaxFunction());
        this.env.init("date", new DateFunction());
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

    @Override
    public Object eval(float expression) {
        return DecimalValue.of(expression);
    }

    @Override
    public Object eval(Expression expression) {
        return executeBlock(expression, env);
    }

    @Override
    public Object eval(NumericLiteral expression) {
        return switch (expression.getKind()) {
            case IntegerLiteral -> IntegerValue.of(expression);
            case DecimalLiteral -> DecimalValue.of(expression);
            default -> throw new IllegalStateException("Unexpected value: " + expression.getKind());
        };
    }

    @Override
    public Object eval(BooleanLiteral expression) {
        return BooleanValue.of(expression);
    }

    @Override
    public Object eval(Identifier expression) {
        return env.lookup(expression.getSymbol());
    }

    @Override
    public Object eval(NullLiteral expression) {
        return null;
    }

    @Override
    public Object eval(StringLiteral expression) {
        return StringValue.of(expression);
    }

    @Override
    public Object eval(LambdaExpression expression) {
        List<Expression> params = expression.getParams();
        Statement body = expression.getBody();
        return FunValue.of((Identifier) null, params, body, env);
    }

    @Override
    public Object eval(BlockExpression block) {
        Object res = NullValue.of();
        var env = new Environment(this.env);
        for (var it : block.getExpression()) {
            res = executeBlock(it, env);
        }
        return res;
    }

    @Override
    public Object eval(VariableStatement statement) {
        Object res = NullValue.of();
        for (var it : statement.getDeclarations()) {
            res = executeBlock(it, env);
        }
        return res;
    }

    @Override
    public Object eval(GroupExpression expression) {
        return null;
    }

    @Override
    public Object eval(BinaryExpression expression) {
        Object lhs = executeBlock(expression.getLeft(), env);
        Object rhs = executeBlock(expression.getRight(), env);
        if ((Object) expression.getOperator() instanceof String op) {
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
            } else if (lhs instanceof DecimalValue lhsn && rhs instanceof DecimalValue rhsn) {
                return switch (op) {
                    case "+" -> DecimalValue.of(lhsn.getValue() + rhsn.getValue());
                    case "-" -> DecimalValue.of(lhsn.getValue() - rhsn.getValue());
                    case "/" -> DecimalValue.of(lhsn.getValue() / rhsn.getValue());
                    case "*" -> DecimalValue.of(lhsn.getValue() * rhsn.getValue());
                    case "%" -> DecimalValue.of(lhsn.getValue() % rhsn.getValue());
                    case "==" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case "<" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0);
                    case "<=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case ">" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0);
                    case ">=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            } else if (lhs instanceof DecimalValue lhsn && rhs instanceof IntegerValue rhsn) {
                return switch (op) {
                    case "+" -> DecimalValue.of(lhsn.getValue() + rhsn.getValue());
                    case "-" -> DecimalValue.of(lhsn.getValue() - rhsn.getValue());
                    case "/" -> DecimalValue.of(lhsn.getValue() / rhsn.getValue());
                    case "*" -> DecimalValue.of(lhsn.getValue() * rhsn.getValue());
                    case "%" -> DecimalValue.of(lhsn.getValue() % rhsn.getValue());
                    case "==" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case "<" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0);
                    case "<=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case ">" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0);
                    case ">=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            } else if (lhs instanceof IntegerValue lhsn && rhs instanceof DecimalValue rhsn) {
                return switch (op) {
                    case "+" -> DecimalValue.of(lhsn.getValue() + rhsn.getValue());
                    case "-" -> DecimalValue.of(lhsn.getValue() - rhsn.getValue());
                    case "/" -> DecimalValue.of(lhsn.getValue() / rhsn.getValue());
                    case "*" -> DecimalValue.of(lhsn.getValue() * rhsn.getValue());
                    case "%" -> DecimalValue.of(lhsn.getValue() % rhsn.getValue());
                    case "==" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case "<" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0);
                    case "<=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) < 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    case ">" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0);
                    case ">=" -> BooleanValue.of(Double.compare(lhsn.getValue(), rhsn.getValue()) > 0 ||
                                                 Double.compare(lhsn.getValue(), rhsn.getValue()) == 0);
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            }
        }
        return null;
    }

    @Override
    public Object eval(CallExpression<Expression> expression) {
        var callee = executeBlock(expression.getCallee(), env);
        if (callee instanceof Callable function) {
            List<Expression> arguments = expression.getArguments();
            var args = new ArrayList<>(arguments.size());
            for (Expression it : arguments) {
                args.add(executeBlock(it, env));
            }

            return function.call(this, args);
        }
        throw new RuntimeError(new Token(expression.getCallee(), TokenType.Fun), "Can only call functions and classes.");
    }

    public Object Call(FunValue function, List<Object> args) {
        if (function.name() == null) { // execute lambda
            return lambdaCall(function, args);
        }

        return functionCall(function, args);
    }

    private Object functionCall(FunValue function, List<Object> args) {
        // for function execution, use the clojured environment from the declared scope
        var declared = (FunValue) function.getEnvironment()
                .lookup(function.name(), "Function not declared: " + function.name());

        if (args.size() != declared.arity()) {
            throw new RuntimeException("Expected %s arguments but got %d: %s".formatted(function.getParams().size(), args.size(), function.getName()));
        }

        var environment = new ActivationEnvironment(declared.getEnvironment(), declared.getParams(), args);
        return executeBlock(declared.getBody(), environment);
    }

    private Object lambdaCall(FunValue function, List<Object> args) {
        Environment activationEnvironment = new ActivationEnvironment(function.getEnvironment(), function.getParams(), args);
        return executeBlock(function.getBody(), activationEnvironment);
    }

    @Override
    public Object eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Object eval(LogicalExpression expression) {
        var left = eval(expression.getLeft());

        if (expression.getOperator() == TokenType.Logical_Or) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }

        return eval(expression.getRight());
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean b) {
            return b;
        } else if (object instanceof BooleanValue b) {
            return b.isValue();
        }
        return true;
    }

    @Override
    public Object eval(MemberExpression expression) {
        if (expression.getProperty() instanceof Identifier resourceName) {
            var value = (IEnvironment) executeBlock(expression.getObject(), env);

            String symbol = resourceName.getSymbol();
            return value.lookup(symbol);
        }
        throw new OperationNotImplementedException("Membership expression not implemented for: " + expression.getObject());
    }


    @Override
    public Object eval(ResourceExpression expression) {
        if (expression.getName() == null) {
            throw new InvalidInitException("Resource does not have a name: " + expression.getType().getSymbol());
        }
        var schemaValueTmp = executeBlock(expression.getType(), env);
        var schemaValue = (SchemaValue) schemaValueTmp;

        Environment schemaEnvironment = Optional.ofNullable(schemaValue.getEnvironment()).orElse(env);
        var resourceEnv = Environment.copyOf(schemaEnvironment);
        try {
            var args = new ArrayList<>();
            for (Statement it : expression.getArguments()) {
                Object objectRuntimeValue = executeBlock(it, resourceEnv);
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
    public Object eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Object eval(IfStatement statement) {
        var eval = (BooleanValue) executeBlock(statement.getTest(), env);
        if (eval.isValue()) {
            return executeBlock(statement.getConsequent(), env);
        } else {
            return executeBlock(statement.getAlternate(), env);
        }
    }

    @Override
    public Object eval(WhileStatement statement) {
        Object result = NullValue.of();

        while (isTruthy(eval(statement.getTest()))) {
            result = executeBlock(statement.getBody(), env);
        }
        return result;
    }

    @Override
    public Object eval(ForStatement statement) {
        Expression test = Optional.ofNullable(statement.getTest()).orElse(BooleanLiteral.of(true));
        var whileStatement = WhileStatement.of(test, BlockExpression.of(statement.getBody(), ExpressionStatement.of(statement.getUpdate())));
        if (statement.getInit() == null) {
            return executeBlock(whileStatement, env);
        }
        return executeBlock(BlockExpression.of(statement.getInit(), whileStatement), env);
    }

    @Override
    public Object eval(SchemaDeclaration expression) {
        var name = expression.getName();
        var schemaEnv = new Environment(env);

        Statement body = expression.getBody();
        if (body instanceof ExpressionStatement statement && statement.getStatement() instanceof BlockExpression blockExpression) {
            executeBlock(blockExpression.getExpression(), schemaEnv); // install properties/methods of a schema into the environment
            return env.init(name.getSymbol(), SchemaValue.of(name, blockExpression, schemaEnv)); // install the schema into the global env
        }
        throw new RuntimeException("Invalid schema");
    }

    @Override
    public Object eval(UnaryExpression expression) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(1 + r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(1 + r.getRuntimeValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "--" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(r.getRuntimeValue() - 1);
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(
                                BigDecimal.valueOf(r.getRuntimeValue()).subtract(BigDecimal.ONE)
                                        .doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "-" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof IntegerValue r) {
                        yield IntegerValue.of(-r.getRuntimeValue());
                    } else if (res instanceof DecimalValue r) {
                        yield DecimalValue.of(BigDecimal.valueOf(r.getRuntimeValue()).negate().doubleValue());
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "!" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof BooleanValue r) {
                        yield BooleanValue.of(!r.isValue());
                    }
                    throw new RuntimeException("Invalid not operator: " + res);
                }
                default -> throw new RuntimeException("Operator could not be evaluated: " + expression.getOperator());
            };
        }
        throw new RuntimeException("Operator could not be evaluated");
    }

    @Override
    public Object eval(VariableDeclaration expression) {
        String symbol = expression.getId().getSymbol();
        Object value = null;
        if (expression.hasInit()) {
            value = executeBlock(expression.getInit(), env);
        }
        return env.init(symbol, value);
    }

    @Override
    public Object eval(AssignmentExpression expression) {
        Object right = executeBlock(expression.getRight(), env);

        Expression left = expression.getLeft();
        if (left instanceof MemberExpression memberExpression) {
            var instanceEnv = (IEnvironment) executeBlock(memberExpression.getObject(), env);
            Expression property = memberExpression.getProperty();
            if (property instanceof Identifier identifier) {
                return instanceEnv.assign(identifier.getSymbol(), right);
            } else {
                throw new OperationNotImplementedException("Invalid assignment expression");
            }
        } else if (left instanceof Identifier identifier) {
            return env.assign(identifier.getSymbol(), right);
        }
        throw new RuntimeException("Invalid assignment");
    }

    @Override
    public Object eval(Program program) {
        Object lastEval = new NullValue();

        for (Statement i : program.getBody()) {
            lastEval = executeBlock(i, env);
        }

        return lastEval;
    }

    @Override
    public Object eval(Statement statement) {
        return execute(statement);
    }

    @Override
    public Object eval(InitStatement statement) {
        var name = statement.getName();
        var params = statement.getParams();
        var body1 = statement.getBody();
        return executeBlock(FunctionDeclaration.of(name, params, body1), env);
    }

    @Override
    public Object eval(FunctionDeclaration declaration) {
        var name = declaration.getName();
        var params = declaration.getParams();
        var body = declaration.getBody();
        return env.init(name.getSymbol(), FunValue.of(name, params, body, env));
    }

    @Override
    public Object eval(ExpressionStatement statement) {
        return executeBlock(statement.getStatement(), env);
    }

    Object interpret(List<Statement> statements) {
        try {
            Object res = null;
            for (Statement statement : statements) {
                res = execute(statement);
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
