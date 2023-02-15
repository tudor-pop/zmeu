package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.*;
import dev.fangscl.ast.TypeSystem.Base.Statement;
import dev.fangscl.ast.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.ast.TypeSystem.Literals.DecimalLiteral;
import dev.fangscl.ast.TypeSystem.Literals.IntegerLiteral;
import dev.fangscl.ast.TypeSystem.Program;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Interpreter {

    public RuntimeValue eval(Statement statement) {
        return switch (statement.getKind()) {
            case DecimalLiteral -> new DecimalValue(((DecimalLiteral) statement).getValue());
            case IntegerLiteral -> new IntegerValue(((IntegerLiteral) statement).getValue());
            case NullLiteral -> new NullValue();
            case BinaryExpression -> eval((BinaryExpression) statement);
            case Program -> eval((Program) statement);
            default -> {
                log.error("This AST does cannot be interpreted yet {}", statement);
                System.exit(1);
                yield null;
            }
        };
    }

    private RuntimeValue eval(BinaryExpression expression) {
        var lhs = eval(expression.getLeft());
        var rhs = eval(expression.getRight());
        if (lhs.getType() == ValueType.Number && rhs.getType() == ValueType.Number) {
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

    private RuntimeValue eval(Program expression) {
        RuntimeValue lastEval = new NullValue();

        for (var i : expression.getBody()) {
            lastEval = eval(i);
        }

        return lastEval;
    }
}
