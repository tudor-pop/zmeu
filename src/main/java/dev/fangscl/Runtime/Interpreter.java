package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Interpreter {
    private final Environment global;

    public Interpreter(Environment global) {
        this.global = global;
        this.global.declareVar("null", new NullValue());
        this.global.declareVar("true", new BooleanValue(true));
        this.global.declareVar("false", new BooleanValue(false));
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
            case StringLiteral -> new StringValue((StringLiteral) statement);
            case IntegerLiteral -> new IntegerValue((NumericLiteral) statement);
            case DecimalLiteral -> new DecimalValue((NumericLiteral) statement);
            case BooleanLiteral -> eval((BinaryExpression) statement, env);
            case Identifier -> env.evaluateVar(((Identifier) statement).getSymbol());
            default -> throw new RuntimeException("error");
        };
    }

    public RuntimeValue eval(int expression) {
        return new IntegerValue(expression);
    }

    public RuntimeValue eval(String expression) {
        return new StringValue(expression);
    }

    public RuntimeValue eval(boolean expression) {
        return new BooleanValue(expression);
    }

    public RuntimeValue eval(float value) {
        return eval((double) value);
    }

    public RuntimeValue eval(double expression) {
        return new DecimalValue(expression);
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
