package io.zmeu.Frontend.Lexical;

import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Runtime.Interpreter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Visitor<Void> {
    private final Interpreter interpreter;
    /**
     * Tracks how many scopes are we nested within source code. Based on this we know how to properly handle variable declarations/resolution
     * boolean = false => variable declared but not ready to be used
     * boolean = true => variable declared and ready to be used
     * */
    private final Stack<Map<Identifier, Boolean>> scopes = new Stack<>();
    /**
     * Tracks weather we're in a function or not. Based on this we show a syntax
     * error for example when using a return statement outside a function
     */
    private FunctionType currentFunction = FunctionType.NONE;


    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    void resolve(List<Statement> statements) {
        for (var statement : statements) {
            resolve(statement);
        }
    }

    public void resolve(Program program) {
        for (var statement : program.getBody()) {
            resolve(statement);
        }
    }

    private void resolve(@NotNull Statement stmt) {
        stmt.accept(this);
    }

    private void resolve(@NotNull Expression expr) {
        expr.accept(this);
    }

    @Override
    public Void eval(Expression expression) {
        resolve(expression);
        return null;
    }

    @Override
    public Void eval(NumberLiteral expression) {
        return null;
    }

    @Override
    public Void eval(BooleanLiteral expression) {
        return null;
    }

    @Override
    public Void eval(/* VariableExpression*/ Identifier identifier) {
        if (!scopes.isEmpty() && scopes.peek().get(identifier.getSymbol()) == Boolean.FALSE) {
            throw ErrorSystem.error("Can't read local variable in its own initializer: " + identifier.getSymbol());
        }

        resolveLocal(identifier);
        return null;
    }

    private void resolveLocal(Identifier identifier) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identifier)) {
                identifier.setHops(scopes.size() - 1 - i);
                break;
            }
        }

    }

    @Override
    public Void eval(BlockExpression expression) {
        beginScope();
        resolve(expression.getExpression());
        endScope();
        return null;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    @Override
    public Void eval(GroupExpression expression) {
        resolve(expression.getExpression());
        return null;
    }

    @Override
    public Void eval(BinaryExpression expression) {
        resolve(expression.getLeft());
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void eval(CallExpression<Expression> expression) {
        resolve(expression.getCallee());
        for (Expression argument : expression.getArguments()) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Void eval(LogicalExpression expression) {
        resolve(expression.getLeft());
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void eval(MemberExpression expression) {
        // Since properties are looked up dynamically, they don’t get resolved
        resolve(expression.getObject());
        return null;
    }

    @Override
    public Void eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Void eval(UnaryExpression expression) {
        resolve(expression.getValue());
        return null;
    }

    @Override
    public Void eval(Program program) {
        return null;
    }

    @Override
    public Void eval(Statement statement) {
        resolve(statement);
        return null;
    }

    @Override
    public Void eval(InitStatement statement) {
        return null;
    }

    @Override
    public Void eval(LambdaExpression expression) {
        resolveFunction(expression.getParams(), expression.getBody(), FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void eval(FunctionDeclaration statement) {
        declare(statement.getName());
        define(statement.getName());

        resolveFunction(statement.getParams(), statement.getBody(), FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(List<Identifier> params, Statement body, FunctionType functionType) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;

        resolveFunction(params, body);

        currentFunction = enclosingFunction;
    }

    private void resolveFunction(List<Identifier> params, Statement body) {
        beginScope(); // one for activation environment
        initParams(params);

        resolveNoBlock(body); //bypass function scope because we already are in the activation environment scope
        endScope();
    }

    private void initParams(List<Identifier> function) {
        for (var param : function) {
            declare(param);
            define(param);
        }
    }

    /*
     this installs the params in the same scope as the function's body.
     without extracting and casting to BlockExpression it would create a separate
     scope for the body and a separate one for the params
     */
    private void resolveNoBlock(Statement statement) {
        if (statement instanceof ExpressionStatement body) {
            if (body.getStatement() instanceof BlockExpression block) {
                resolve(block.getExpression());
            } else {
                resolve(body.getStatement());
            }
        } else {
            throw new RuntimeException("oops: Statement is not an expression statement: " + statement.toString());
        }
    }

    @Override
    public Void eval(ExpressionStatement statement) {
        resolve(statement.getStatement());
        return null;
    }

    @Override
    public Void eval(VariableStatement statement) {
        for (VariableDeclaration declaration : statement.getDeclarations()) {
            eval(declaration);
        }
        return null;
    }

    /**
     * Here, we see how resolution is different from interpretation. When we resolve an if statement, there is no control flow.
     * We resolve the condition and both branches. Where a dynamic execution steps only into the branch that is run,
     * a static analysis is conservative—it analyzes any branch that could be run.
     * Since either one could be reached at runtime, we resolve both.
     */
    @Override
    public Void eval(IfStatement statement) {
        resolve(statement.getTest());
        resolve(statement.getConsequent());
        if (statement.hasElse()) {
            resolve(statement.getAlternate());
        }
        return null;
    }

    /**
     * As in if statements, with a while statement, we resolve its condition and resolve the body exactly once.
     */
    @Override
    public Void eval(WhileStatement statement) {
        resolve(statement.getTest());
        resolve(statement.getBody());
        return null;
    }

    @Override
    public Void eval(ResourceExpression expression) {
        beginScope();
        if (expression.getName() != null) {
            declare(expression.getName());
            define(expression.getName());
            resolve(expression.getName());
        }
        for (Statement argument : expression.getArguments()) {
            if (argument instanceof ExpressionStatement statement) {
                if (statement.getStatement() instanceof AssignmentExpression assignmentExpression) {
                    if (assignmentExpression.getLeft() instanceof Identifier identifier) {
                        declare(identifier);
                        define(identifier);
//                        resolve(identifier);
                    }
                }
            }
        }
//        resolve(expression.getArguments());
        endScope();
        return null;
    }

    @Override
    public Void eval(ForStatement statement) {
        beginScope();
        if (statement.getInit() != null) {
            resolve(statement.getInit());
        }
        resolve(statement.getTest());
        resolve(statement.getUpdate());
        resolveNoBlock(statement.getBody()); // we are already inside the block opened above
        endScope();
        return null;
    }

    @Override
    public Void eval(SchemaDeclaration statement) {
//        beginScope();
//        resolve(statement.getName());
//        resolveNoBlock(statement.getBody());
//        endScope();
        return null;
    }

    @Override
    public Void eval(ReturnStatement statement) {
        if (currentFunction == FunctionType.NONE) {
            throw ErrorSystem.error("Can't return from top level code");
        }
        if (statement.hasArgument()) {
            resolve(statement.getArgument());
        }
        return null;
    }

    @Override
    public Void eval(VariableDeclaration declaration) {
        declare(declaration.getId());
        if (declaration.hasInit()) {
            resolve(declaration.getInit());
        }
        define(declaration.getId());
        return null;
    }

    /**
     * Declaration adds the variable to the innermost scope so that it shadows any outer one and so that we know the variable exists. We mark it as “not ready yet” by binding its name to false in the scope map.
     * The value associated with a key in the scope map represents whether or not we have finished resolving that variable’s initializer.
     */
    private void declare(Identifier name) {
        if (scopes.isEmpty()) return;

        Map<Identifier, Boolean> scope = scopes.peek();

        if (scope.containsKey(name)) {
            throw ErrorSystem.error("Already a variable with this name in this scope: " + name.getSymbol());
        }

        scope.put(name, false);
    }

    /**
     * After declaring the variable, we resolve its initializer expression in that same scope where the new variable now exists but is unavailable.
     * Once the initializer expression is done, the variable is ready for prime time. We do that by defining it.
     */
    private void define(Identifier name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name, true);
    }

    @Override
    public Void eval(AssignmentExpression expression) {
        resolve(expression.getRight());
        resolve(expression.getLeft());
        return null;
    }

    @Override
    public Void eval(float expression) {
        return null;
    }

    @Override
    public Void eval(double expression) {
        return null;
    }

    @Override
    public Void eval(int expression) {
        return null;
    }

    @Override
    public Void eval(boolean expression) {
        return null;
    }

    @Override
    public Void eval(String expression) {
        return null;
    }


    @Override
    public Void eval(NullLiteral expression) {
        return null;
    }

    @Override
    public Void eval(StringLiteral expression) {
        return null;
    }

}
