package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.*;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Literals.BooleanLiteral;
import dev.fangscl.Runtime.TypeSystem.Literals.DecimalLiteral;
import dev.fangscl.Runtime.TypeSystem.Literals.Identifier;
import dev.fangscl.Runtime.TypeSystem.Literals.IntegerLiteral;
import dev.fangscl.Runtime.TypeSystem.Program;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Interpreter {
    private final Environment global;

    public Interpreter(Environment global) {
        this.global = global;
//        this.global.declareVar("null", new NullValue());
        this.global.declareVar("true", new BooleanValue(true));
        this.global.declareVar("false", new BooleanValue(false));
    }

    public Interpreter() {
        this(new Environment());
    }

    private RuntimeValue eval(Program expression, Environment environment) {
        RuntimeValue lastEval = new NullValue();

        for (var i : expression.getBody()) {
            lastEval = eval(i, environment);
        }

        return lastEval;
    }

    public RuntimeValue eval(Statement expression) {
        return this.eval(expression, this.global);
    }

    public RuntimeValue eval(Statement statement, Environment env) {
        return switch (statement.getKind()) {
            case DecimalLiteral -> new DecimalValue(((DecimalLiteral) statement).getValue());
            case IntegerLiteral -> new IntegerValue(((IntegerLiteral) statement).getValue());
            case BooleanLiteral -> new BooleanValue(((BooleanLiteral) statement).isValue());
            case Identifier -> env.evaluateVar(((Identifier) statement).getSymbol());
            case NullLiteral -> new NullValue();
            case BinaryExpression -> eval((BinaryExpression) statement, env);
            case Program -> eval((Program) statement, env);
            default -> {
                log.debug("This AST does cannot be interpreted yet {}", statement);
                System.exit(1);
                yield null;
            }
        };
    }

    private RuntimeValue eval(BinaryExpression expression, Environment environment) {
        var lhs = eval(expression.getLeft(), environment);
        var rhs = eval(expression.getRight(), environment);
        if ((lhs.getType() == ValueType.Decimal && rhs.getType() == ValueType.Decimal) ||
                (lhs.getType() == ValueType.Integer && rhs.getType() == ValueType.Integer)) {
            return eval(lhs, rhs, expression.getOperator());
        }
        return new NullValue();
    }

    private RuntimeValue eval(RuntimeValue lhs, RuntimeValue rhs, String operator) {
        if (lhs instanceof IntegerValue lhsn && rhs instanceof IntegerValue rhsn) {
            var res = switch (operator) {
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
