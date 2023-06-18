package dev.fangscl.Runtime;

import dev.fangscl.ErrorSystem;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Visitor<Void>, dev.fangscl.Frontend.Parser.Statements.Visitor<Void> {
    private final Interpreter interpreter;
    /*
     * boolean = false => variable declared but not ready to be used
     * boolean = true => variable declared and ready to be used
     * */
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    void resolve(List<Statement> statements) {
        for (var statement : statements) {
            resolve(statement);
        }
    }
    void resolve(Program program) {
        for (var statement : program.getBody()) {
            resolve(statement);
        }
    }

    private void resolve(Statement stmt) {
        stmt.accept(this);
    }

    private void resolve(Expression expr) {
        expr.accept(this);
    }

    @Override
    public Void eval(Expression expression) {
        resolve(expression);
        return null;
    }

    @Override
    public Void eval(NumericLiteral expression) {
        return null;
    }

    @Override
    public Void eval(BooleanLiteral expression) {
        return null;
    }

    @Override
    public Void eval(/* VariableExpression*/ Identifier identifier) {
        if (!scopes.isEmpty() && scopes.peek().get(identifier.getSymbol()) == Boolean.FALSE) {
            ErrorSystem.error("Can't read local variable in its own initializer.", identifier.getSymbol());
        }

        resolveLocal(identifier, identifier);
        return null;
    }

    private void resolveLocal(Expression expression, Identifier identifier) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identifier.getSymbol())) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                break;
            }
        }

    }

    @Override
    public Void eval(ResourceExpression expression) {
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

    @Override
    public Void eval(LambdaExpression expression) {
        beginScope();
        for (Identifier param : expression.getParams()) {
            declare(param);
            define(param);
        }
        resolve(expression.getBody());
        endScope();
        return null;
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
    public Void eval(FunctionDeclaration statement) {
        declare(statement.getName());
        define(statement.getName());

        resolveFunction(statement);
        return null;
    }

    private void resolveFunction(FunctionDeclaration function) {
        beginScope();
        for (var param : function.getParams()) {
            declare(param);
            define(param);
        }
        resolve(function.getBody());
        endScope();
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
    public Void eval(ForStatement statement) {
        return null;
    }

    @Override
    public Void eval(TypeDeclaration statement) {
        return null;
    }

    @Override
    public Void eval(ReturnStatement statement) {
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

        Map<String, Boolean> scope = scopes.peek();
        scope.put(name.getSymbol(), false);
    }

    /**
     * After declaring the variable, we resolve its initializer expression in that same scope where the new variable now exists but is unavailable.
     * Once the initializer expression is done, the variable is ready for prime time. We do that by defining it.
     */
    private void define(Identifier name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.getSymbol(), true);
    }

    @Override
    public Void eval(AssignmentExpression expression) {
        resolve(expression.getRight());
        resolveLocal(expression, (Identifier) expression.getLeft());
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
}
