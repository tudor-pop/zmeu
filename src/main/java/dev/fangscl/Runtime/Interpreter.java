package dev.fangscl.Runtime;

import dev.fangscl.Engine.Engine;
import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Environment.ActivationEnvironment;
import dev.fangscl.Runtime.Environment.Environment;
import dev.fangscl.Runtime.Environment.IEnvironment;
import dev.fangscl.Runtime.Functions.Cast.BooleanCastFunction;
import dev.fangscl.Runtime.Functions.Cast.DecimalCastFunction;
import dev.fangscl.Runtime.Functions.Cast.IntCastFunction;
import dev.fangscl.Runtime.Functions.Cast.StringCastFunction;
import dev.fangscl.Runtime.Functions.DateFunction;
import dev.fangscl.Runtime.Functions.Numeric.*;
import dev.fangscl.Runtime.Functions.PrintFunction;
import dev.fangscl.Runtime.Functions.PrintlnFunction;
import dev.fangscl.Runtime.Values.FunValue;
import dev.fangscl.Runtime.Values.NullValue;
import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.TypeValue;
import dev.fangscl.Runtime.exceptions.*;
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
    private final Engine engine;

    public Interpreter() {
        this(new Environment());
    }

    public Interpreter(Environment environment) {
        this(environment, new Engine());
    }

    public Interpreter(Environment environment, Engine engine) {
        this.engine = engine;
        this.env = environment;
        this.env.init("null", NullValue.of());
        this.env.init("true", true);
        this.env.init("false", false);
        this.env.init("print", new PrintFunction());
        this.env.init("println", new PrintlnFunction());

        // casting
        this.env.init("int", new IntCastFunction());
        this.env.init("decimal", new DecimalCastFunction());
        this.env.init("string", new StringCastFunction());
        this.env.init("boolean", new BooleanCastFunction());

        // number
        this.env.init("pow", new PowFunction());
        this.env.init("min", new MinFunction());
        this.env.init("max", new MaxFunction());
        this.env.init("ceil", new CeilFunction());
        this.env.init("floor", new FloorFunction());
        this.env.init("abs", new AbsFunction());
        this.env.init("date", new DateFunction());

//        this.env.init("Vm", TypeValue.of("Vm", new Environment(env, new Vm())));
    }

    @Override
    public Object eval(int expression) {
        return expression;
    }

    @Override
    public Object eval(boolean expression) {
        return expression;
    }

    @Override
    public Object eval(String expression) {
        return expression;
    }

    @Override
    public Object eval(double expression) {
        return expression;
    }

    @Override
    public Object eval(float expression) {
        return expression;
    }

    @Override
    public Object eval(Expression expression) {
        return executeBlock(expression, env);
    }

    @Override
    public Object eval(NumericLiteral expression) {
        return switch (expression.getKind()) {
            case IntegerLiteral, DecimalLiteral -> expression.getValue();
            default -> throw new IllegalStateException("Unexpected value: " + expression.getKind());
        };
    }

    @Override
    public Object eval(BooleanLiteral expression) {
        return expression.isValue();
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
        return expression.getValue();
    }

    @Override
    public Object eval(LambdaExpression expression) {
        var params = expression.getParams();
        var body = expression.getBody();
        return FunValue.of((Identifier) null, params, body, env);
    }

    @Override
    public Object eval(BlockExpression block) {
        Object res = NullValue.of();
        var env = this.env instanceof ActivationEnvironment ? this.env : new Environment(this.env);
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
            if (lhs instanceof Integer lhsn && rhs instanceof Integer rhsn) {
                return switch (op) {
                    case "+" -> lhsn + rhsn;
                    case "-" -> lhsn - rhsn;
                    case "/" -> lhsn / rhsn;
                    case "*" -> lhsn * rhsn;
                    case "%" -> lhsn % rhsn;
                    case "==" -> lhsn.equals(rhsn);
                    case "<" -> lhsn < rhsn;
                    case "<=" -> lhsn <= rhsn;
                    case ">" -> lhsn > rhsn;
                    case ">=" -> lhsn >= rhsn;
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            } else if (lhs instanceof Double lhsn && rhs instanceof Double rhsn) {
                return switch (op) {
                    case "+" -> lhsn + rhsn;
                    case "-" -> lhsn - rhsn;
                    case "/" -> lhsn / rhsn;
                    case "*" -> lhsn * rhsn;
                    case "%" -> lhsn % rhsn;
                    case "==" -> Double.compare(lhsn, rhsn) == 0;
                    case "<" -> Double.compare(lhsn, rhsn) < 0;
                    case "<=" -> Double.compare(lhsn, rhsn) < 0 || Double.compare(lhsn, rhsn) == 0;
                    case ">" -> Double.compare(lhsn, rhsn) > 0;
                    case ">=" -> Double.compare(lhsn, rhsn) > 0 || Double.compare(lhsn, rhsn) == 0;
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            } else if (lhs instanceof Double lhsn && rhs instanceof Integer rhsn) {
                return switch (op) {
                    case "+" -> lhsn + rhsn;
                    case "-" -> lhsn - rhsn;
                    case "/" -> lhsn / rhsn;
                    case "*" -> lhsn * rhsn;
                    case "%" -> lhsn % rhsn;
                    case "==" -> Double.compare(lhsn, rhsn) == 0;
                    case "<" -> Double.compare(lhsn, rhsn) < 0;
                    case "<=" -> Double.compare(lhsn, rhsn) < 0 || Double.compare(lhsn, rhsn) == 0;
                    case ">" -> Double.compare(lhsn, rhsn) > 0;
                    case ">=" -> Double.compare(lhsn, rhsn) > 0 || Double.compare(lhsn, rhsn) == 0;
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            } else if (lhs instanceof Integer lhsn && rhs instanceof Double rhsn) {
                return switch (op) {
                    case "+" -> lhsn + rhsn;
                    case "-" -> lhsn - rhsn;
                    case "/" -> lhsn / rhsn;
                    case "*" -> lhsn * rhsn;
                    case "%" -> lhsn % rhsn;
                    case "==" -> Double.compare(lhsn, rhsn) == 0;
                    case "<" -> Double.compare(lhsn, rhsn) < 0;
                    case "<=" -> Double.compare(lhsn, rhsn) < 0 || Double.compare(lhsn, rhsn) == 0;
                    case ">" -> Double.compare(lhsn, rhsn) > 0;
                    case ">=" -> Double.compare(lhsn, rhsn) > 0 || Double.compare(lhsn, rhsn) == 0;
                    default -> throw new RuntimeException("Operator could not be evaluated");
                };
            }
        }
        throw new RuntimeException("Invalid number: %s %s".formatted(lhs, rhs));
    }

    @Override
    public Object eval(CallExpression<Expression> expression) {
        var callee = executeBlock(expression.getCallee(), env);
        if (callee instanceof Callable function) {

            // evaluate arguments
            var args = new ArrayList<>(expression.getArguments().size());
            for (Expression it : expression.getArguments()) {
                args.add(executeBlock(it, env));
            }

            try {
                return function.call(this, args);
            } catch (Return aReturn) {
                return aReturn.getValue();
            }
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
        var declared = (FunValue) function.getClojure().lookup(function.name(), "Function not declared: %s".formatted(function.name()));

        if (args.size() != declared.arity()) {
            throw new RuntimeException("Expected %s arguments but got %d: %s".formatted(function.getParams().size(), args.size(), function.getName()));
        }

        var environment = new ActivationEnvironment(declared.getClojure(), declared.getParams(), args);
        return executeBlock(declared.getBody(), environment);
    }

    private Object lambdaCall(FunValue function, List<Object> args) {
        var environment = new ActivationEnvironment(function.getClojure(), function.getParams(), args);
        return executeBlock(function.getBody(), environment);
    }

    @Override
    public Object eval(ReturnStatement statement) {
        Object value = null;
        if (statement.getArgument() != null) {
            value = eval(statement.getArgument());
        }
        throw new Return(value);
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
        }
        return true;
    }

    @Override
    public Object eval(MemberExpression expression) {
        if (expression.getProperty() instanceof Identifier resourceName) {
            var value = (IEnvironment) executeBlock(expression.getObject(), env);
            // when retrieving the type of a resource, we first check the "instances" field for existing resources initialised there
            // Since that environment points to the parent(type env) it will also find the properties
            if (value instanceof TypeValue typeValue) {
                value = typeValue.getInstances();
            } // else it could be a resource or any other type
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
        var typeValue = (TypeValue) executeBlock(expression.getType(), env);

        Environment typeEnvironment = typeValue.getEnvironment();
        Environment resourceEnv = new Environment(typeEnvironment, typeEnvironment.getVariables());
        try {
            var init = typeValue.getMethodOrNull("init");
            if (init != null) {
                var args = new ArrayList<>();
                for (Statement it : expression.getArguments()) {
                    Object objectRuntimeValue = executeBlock(it, resourceEnv);
                    args.add(objectRuntimeValue);
                }
                functionCall(FunValue.of(init.name(), init.getParams(), init.getBody(), resourceEnv/* this env */), args);
            } else {
                expression.getArguments().forEach(it -> executeBlock(it, resourceEnv));
            }
            var res = typeValue.initInstance(expression.name(), ResourceValue.of(expression.name(), resourceEnv));
            engine.process(typeValue.typeString(), resourceEnv.getVariables());
            return res;
        } catch (NotFoundException e) {
//            throw new NotFoundException("Field '%s' not found on resource '%s'".formatted(e.getObjectNotFound(), expression.name()),e);
            throw e;
        }
    }

    @Override
    public Object eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Object eval(IfStatement statement) {
        var eval = (Boolean) executeBlock(statement.getTest(), env);
        if (eval) {
            return executeBlock(statement.getConsequent(), env);
        } else {
            Statement alternate = statement.getAlternate();
            if (alternate == null) {
                return null;
            }
            return executeBlock(alternate, env);
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
    public Object eval(TypeDeclaration expression) {
        var name = expression.getName();
        var typeEnv = new Environment(env);

        Statement body = expression.getBody();
        if (body instanceof ExpressionStatement statement && statement.getStatement() instanceof BlockExpression blockExpression) {
            executeBlock(blockExpression.getExpression(), typeEnv); // install properties/methods of a type into the environment
            return env.init(name, TypeValue.of(name, typeEnv)); // install the type into the global env
        }
        throw new RuntimeException("Invalid type");
    }

    @Override
    public Object eval(UnaryExpression expression) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof Integer integer) {
                        yield 1 + integer;
                    } else if (res instanceof Double aDouble) {
                        yield 1 + aDouble;
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "--" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof Integer integer) {
                        yield integer - 1;
                    } else if (res instanceof Double aDouble) {
                        yield BigDecimal.valueOf(aDouble).subtract(BigDecimal.ONE).doubleValue();
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "-" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof Integer r) {
                        yield -r;
                    } else if (res instanceof Double r) {
                        yield BigDecimal.valueOf(r).negate().doubleValue();
                    } else {
                        throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "!" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    if (res instanceof Boolean aBoolean) {
                        yield !aBoolean;
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
        return eval(FunctionDeclaration.of(statement.getName(), statement.getParams(), statement.getBody()));
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
