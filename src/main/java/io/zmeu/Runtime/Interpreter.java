package io.zmeu.Runtime;

import io.zmeu.Engine.ResourceManager;
import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Runtime.Environment.ActivationEnvironment;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Functions.Cast.BooleanCastFunction;
import io.zmeu.Runtime.Functions.Cast.DecimalCastFunction;
import io.zmeu.Runtime.Functions.Cast.IntCastFunction;
import io.zmeu.Runtime.Functions.Cast.StringCastFunction;
import io.zmeu.Runtime.Functions.DateFunction;
import io.zmeu.Runtime.Functions.Numeric.*;
import io.zmeu.Runtime.Functions.PrintFunction;
import io.zmeu.Runtime.Functions.PrintlnFunction;
import io.zmeu.Runtime.Values.*;
import io.zmeu.Runtime.exceptions.*;
import io.zmeu.TypeChecker.Types.Type;
import io.zmeu.Visitors.LanguageAstPrinter;
import io.zmeu.Visitors.Visitor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static io.zmeu.Frontend.Parser.Statements.FunctionDeclaration.fun;
import static io.zmeu.Utils.BoolUtils.isTruthy;

@Log4j2
public final class Interpreter implements Visitor<Object> {
    private static boolean hadRuntimeError;
    private Environment<Object> env;
    @Getter
    private final HashMap<String, ResourceValue> resources = new HashMap<>();
    private final LanguageAstPrinter printer = new LanguageAstPrinter();
    private final DeferredObservable deferredObservable = new DeferredObservable();

    public Interpreter() {
        this(new Environment());
    }


    public Interpreter(Environment<Object> environment) {
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

//        this.globals.init("Vm", SchemaValue.of("Vm", new Environment(env, new Vm())));
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
    public Object eval(NumberLiteral expression) {
        return switch (expression) {
            case NumberLiteral literal -> literal.getValue();
        };
    }

    @Override
    public Object eval(BooleanLiteral expression) {
        return expression.isValue();
    }

    @Override
    public Object eval(Identifier expression) {
        return lookupVar(expression);
    }

    private Object lookupVar(Identifier expression) {
        return env.lookup(expression.string(), expression.getHops());
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
        return executeDiscardBlock(declared, environment);
    }

    private Object lambdaCall(FunValue function, List<Object> args) {
        var environment = new ActivationEnvironment(function.getClojure(), function.getParams(), args);
        return executeDiscardBlock(function, environment);
    }

    private Object executeDiscardBlock(FunValue declared, ActivationEnvironment environment) {
        Statement statement = declared.getBody();
        if (statement instanceof ExpressionStatement expressionStatement) {
            Expression expression = expressionStatement.getStatement();
            if (expression instanceof BlockExpression blockExpression) {
                return executeBlock(blockExpression.getExpression(), environment);
            } else { // lambdas without a block could simply be an expression: ((x) -> x*x)
                return executeBlock(expression, environment);
            }
        }
        throw new RuntimeException("Invalid function body");
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
        var right = eval(expression.getRight());

        if (left == null || right == null) {
            throw new IllegalArgumentException("Left expression does not exist: " + printer.eval(expression));
        }
        if (!(left instanceof Boolean) || !(right instanceof Boolean)) {
            throw new IllegalArgumentException("Left expression does not exist: " + printer.eval(expression));
        }

        if (expression.getOperator() == TokenType.Logical_Or) {
            if (isTruthy(left)) {
                return left;
            }
            if (isTruthy(right)) {
                return right;
            }
            return right;
        } else if (expression.getOperator() == TokenType.Logical_And) {
            if (isTruthy(right)) {
                return left;
            }

            if (isTruthy(left)) {
                return right;
            }

            return right;
        }

        throw new IllegalArgumentException("Left expression does not exist: " + printer.eval(expression));
    }

    @Override
    public Object eval(AssignmentExpression expression) {
        switch (expression.getLeft()) {
            case MemberExpression memberExpression -> {
                var instanceEnv = executeBlock(memberExpression.getObject(), env);
                if (instanceEnv instanceof ResourceValue resourceValue) {
                    throw new RuntimeError("Resources can only be updated inside their block: " + resourceValue.getName());
                }
            }
            case SymbolIdentifier identifier -> {
                Object right = executeBlock(expression.getRight(), env);
                //            Integer distance = locals.get(identifier);
                if (right instanceof Dependency dependency) {
                    env.assign(identifier.string(), dependency.value());
                    return dependency;
                }
                return env.assign(identifier.string(), right);
            }
            case null, default -> {
            }
        }
        throw new RuntimeException("Invalid assignment");
    }

    @Override
    public Object eval(MemberExpression expression) {
        if (!(expression.getProperty() instanceof SymbolIdentifier resourceName)) {
            throw new OperationNotImplementedException("Membership expression not implemented for: " + expression.getObject());
        }
        var value = executeBlock(expression.getObject(), env);
        // when retrieving the type of a resource, we first check the "instances" field for existing resources initialised there
        // Since that environment points to the parent(type env) it will also find the properties
        if (value instanceof SchemaValue schemaValue) { // vm.main -> if user references the schema we search for the instances of those schemas
            String name = resourceName.string();
            if (schemaValue.getInstances().get(name) == null) {
                // if instance was not installed yet -> it will be installed later so we return a deferred object
                return new Deferred(schemaValue, name);
            } else {
                return schemaValue.getInstances().lookup(name);
            }
        } else if (value instanceof ResourceValue resourceValue) {
            if (expression.getObject() instanceof MemberExpression) {
                return new Dependency(resourceValue, resourceValue.lookup(resourceName.string()) );
            }
            return resourceValue.lookup(resourceName.string());
        } // else it could be a resource or any other type like a NumericLiteral or something else
        return value;
    }

    @Override
    public Object eval(ResourceExpression resource) {
        if (resource.getName() == null) {
            throw new InvalidInitException("Resource does not have a name: " + resource.name());
        }
        // SchemaValue already installed globally when evaluating a SchemaDeclaration. This means the schema must be declared before the resource
        var installedSchema = (SchemaValue) executeBlock(resource.getType(), env);

        Environment typeEnvironment = installedSchema.getEnvironment();

        var instance = installedSchema.getInstance(resource.name());
        if (instance == null) {
            // clone all properties from schema properties to the new resource
            var resourceEnv = new Environment(typeEnvironment, typeEnvironment.getVariables());
            resourceEnv.remove(SchemaValue.INSTANCES); // instances should not be available to a resource only to it's schema
            instance = new ResourceValue(resource.name(), resourceEnv, installedSchema);
            // init any kind of new resource
            installedSchema.initInstance(resource.name(), instance);
        }
        try {
//            var init = installedSchema.getMethodOrNull("init");
//            if (init != null) {
//                var args = new ArrayList<>();
//                for (Statement it : resource.getArguments()) {
//                    Object objectRuntimeValue = executeBlock(it, resourceEnv);
//                    args.add(objectRuntimeValue);
//                }
//                functionCall(FunValue.of(init.name(), init.getParams(), init.getBody(), resourceEnv/* this env */), args);
//            } else {

            resource.setEvaluated(true);
            for (Statement it : resource.getArguments()) {
                var result = executeBlock(it, instance.getProperties());
                if (result instanceof Deferred deferred) {
                    instance.addDependency(deferred.resource());

                    cycleDetection(it, deferred, installedSchema, resource, instance);

                    resource.setEvaluated(false);

                    deferredObservable.addObserver(resource, deferred);
                } else if (result instanceof Dependency dependency) {
                    instance.addDependency(dependency.resource().getName());
                }
            }
            if (resource.isEvaluated()) {
                deferredObservable.notifyObservers(this, resource.name());
                return resources.put(instance.name(), instance);
            } else {
                // if not fully evaluated, doesn't make sense to notify observers(resources that depend on this resource)
                // because they will not be able to be reevaluated
                return instance;
            }
        } catch (NotFoundException e) {
//            throw new NotFoundException("Field '%s' not found on resource '%s'".formatted(e.getObjectNotFound(), expression.name()),e);
            throw e;
        }
    }

    /**
     * given 2 resources:
     * resource Type x {
     * name = Type.y.name
     * }
     * resource Type y {
     * name = Type.x.name
     * }
     * when y.Type.x.name returns a deferred(y) it means it points to itself
     * because the deferred comes from x which waits for y to be evaluated.
     * This works for:
     * 1. direct cycles: a -> b and b -> a
     * 2. indirect cycles: a->b->c->a
     */
    private void cycleDetection(Statement expression, Deferred deferred, SchemaValue installedSchema, ResourceExpression instance, ResourceValue resource) {
        var deferredResource = installedSchema.getInstance(deferred.resource());
        if (deferredResource == null) {
            return;
        }
        // direct cycle
        if (Objects.equals(instance.name(), deferred.resource())) {
            String message = "Cycle detected between : \n" + printer.eval(expression) + " \n" + printer.eval(instance);
            log.error(message);
            throw new RuntimeException(message);
        }
        // indirect cycle
        if (deferredResource.getDependencies().contains(resource.name())) {
            if (resource.getDependencies().contains(deferred.resource())) {
                String message = "Cycle detected between : \n" + printer.eval(expression) + " \n" + printer.eval(instance);
                log.error(message);
                throw new RuntimeException(message);
            }
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
        List<Statement> statements = statement.discardBlock();
        statements.add(ExpressionStatement.expressionStatement(statement.getUpdate()));
        var whileStatement = WhileStatement.of(statement.getTest(), BlockExpression.block(statements));
        if (statement.getInit() == null) {
            return executeBlock(whileStatement, env);
        }
        return executeBlock(BlockExpression.block(statement.getInit(), whileStatement), env);
    }

    @Override
    public Object eval(SchemaDeclaration expression) {
        switch (expression.getBody()) {
            case ExpressionStatement statement when statement.getStatement() instanceof BlockExpression blockExpression -> {
                var typeEnv = new Environment<>(env);
                executeBlock(blockExpression.getExpression(), typeEnv); // install properties/methods of a type into the environment

                var name = expression.getName();
                return env.init(name.string(), SchemaValue.of(name, typeEnv)); // install the type into the global env
            }
            case null, default -> {
            }
        }
        throw new RuntimeException("Invalid declaration:" + expression.getName());
    }

    @Override
    public Object eval(UnaryExpression expression) {
        Object operator = expression.getOperator();
        if (operator instanceof String op) {
            return switch (op) {
                case "++" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    switch (res) {
                        case Integer integer -> {
                            yield 1 + integer;
                        }
                        case Double aDouble -> {
                            yield 1 + aDouble;
                        }
                        case null, default -> throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "--" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    switch (res) {
                        case Integer integer -> {
                            yield integer - 1;
                        }
                        case Double aDouble -> {
                            yield BigDecimal.valueOf(aDouble).subtract(BigDecimal.ONE).doubleValue();
                        }
                        case null, default -> throw new RuntimeException("Invalid unary operator: " + res);
                    }
                }
                case "-" -> {
                    Object res = executeBlock(expression.getValue(), env);
                    switch (res) {
                        case Integer r -> {
                            yield -r;
                        }
                        case Double r -> {
                            yield BigDecimal.valueOf(r).negate().doubleValue();
                        }
                        case null, default -> throw new RuntimeException("Invalid unary operator: " + res);
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
        String symbol = expression.getId().string();
        Object value = null;
        if (expression.hasInit()) {
            value = executeBlock(expression.getInit(), env);
        }
        if (value instanceof Dependency dependency) { // a dependency access on another resource
            return env.init(symbol, dependency.value());
        }
        return env.init(symbol, value);
    }


    @Override
    public Object eval(Program program) {
        Object lastEval = new NullValue();

        if (ErrorSystem.hadErrors()) {
            return null;
        }
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
    public Object eval(Type type) {
        return type;
    }

    @Override
    public Object eval(InitStatement statement) {
        return eval(fun(statement.getName(), statement.getParams(), statement.getBody()));
    }

    @Override
    public Object eval(FunctionDeclaration declaration) {
        var name = declaration.getName();
        var params = declaration.getParams();
        var body = declaration.getBody();
        return env.init(name.string(), FunValue.of(name, params, body, env));
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
        System.err.printf("%s\n[line %d]%n", error.getMessage(), error.getToken().line());
        hadRuntimeError = true;
    }

}
