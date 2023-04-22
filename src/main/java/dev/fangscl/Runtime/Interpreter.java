package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Interpreter {
    private final Environment global;

    public Interpreter(Environment global) {
        this.global = global;
        this.global.init("null", new NullValue());
        this.global.init("true", BooleanValue.of(true));
        this.global.init("false", BooleanValue.of(false));
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

    public RuntimeValue eval(Statement expression) {
        return this.eval(expression, this.global);
    }

    public RuntimeValue eval(Statement statement, Environment env) {
        return switch (statement.getKind()) {
            case Program -> eval((Program) statement, env);
            case StringLiteral -> StringValue.of(statement);
            case IntegerLiteral -> IntegerValue.of(statement);
            case DecimalLiteral -> DecimalValue.of(statement);
            case ExpressionStatement -> eval(((ExpressionStatement) statement).getExpression(), env);
            case BinaryExpression -> eval((BinaryExpression) statement, env);
            case Identifier, VariableDeclaration -> env.evaluateVar(((Identifier) statement).getSymbol());
            default -> throw new RuntimeException("error");
        };
    }

    public RuntimeValue eval(int expression) {
        return eval(NumericLiteral.of(expression));
    }

    public RuntimeValue eval(String expression) {
        return eval(StringLiteral.of(expression));
    }

    public RuntimeValue eval(boolean expression) {
        return eval(BooleanLiteral.of(expression));
    }

    public RuntimeValue eval(float value) {
        return eval(NumericLiteral.of(value));
    }

    public RuntimeValue eval(double expression) {
        return eval(NumericLiteral.of(expression));
    }

    private RuntimeValue eval(BinaryExpression expression, Environment environment) {
        var lhs = eval(expression.getLeft(), environment);
        var rhs = eval(expression.getRight(), environment);
        return eval(lhs, rhs, expression.getOperator());
    }

    private RuntimeValue eval(RuntimeValue lhs, RuntimeValue rhs, Object operator) {
        if (lhs instanceof IntegerValue lhsn && rhs instanceof IntegerValue rhsn && operator instanceof String op) {
            var res = switch (op) {
                case "+" -> lhsn.getValue() + rhsn.getValue();
                case "-" -> lhsn.getValue() - rhsn.getValue();
                case "/" -> lhsn.getValue() / rhsn.getValue();
                case "*" -> lhsn.getValue() * rhsn.getValue();
                case "%" -> lhsn.getValue() % rhsn.getValue();
                default -> throw new RuntimeException("Operator could not be evaluated");
            };
            return new IntegerValue(res);
        }
        return null;
    }
}
