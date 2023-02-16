package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Scope.Scope;
import dev.fangscl.Runtime.Values.*;
import dev.fangscl.ast.TypeSystem.Base.Statement;
import dev.fangscl.ast.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.ast.TypeSystem.Literals.BooleanLiteral;
import dev.fangscl.ast.TypeSystem.Literals.DecimalLiteral;
import dev.fangscl.ast.TypeSystem.Literals.Identifier;
import dev.fangscl.ast.TypeSystem.Literals.IntegerLiteral;
import dev.fangscl.ast.TypeSystem.Program;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Interpreter {

    public RuntimeValue eval(Program expression, Scope scope) {
        RuntimeValue lastEval = new NullValue();

        for (var i : expression.getBody()) {
            lastEval = eval(i, scope);
        }

        return lastEval;
    }

    private RuntimeValue eval(Statement statement, Scope scope) {
        return switch (statement.getKind()) {
            case DecimalLiteral -> new DecimalValue(((DecimalLiteral) statement).getValue());
            case IntegerLiteral -> new IntegerValue(((IntegerLiteral) statement).getValue());
            case BooleanLiteral -> new BooleanValue(((BooleanLiteral) statement).isValue());
            case Identifier -> scope.evaluateVar(((Identifier) statement).getSymbol());
            case NullLiteral -> new NullValue();
            case BinaryExpression -> eval((BinaryExpression) statement, scope);
            case Program -> eval((Program) statement, scope);
            default -> {
                log.error("This AST does cannot be interpreted yet {}", statement);
                System.exit(1);
                yield null;
            }
        };
    }

    private RuntimeValue eval(BinaryExpression expression, Scope scope) {
        var lhs = eval(expression.getLeft(), scope);
        var rhs = eval(expression.getRight(), scope);
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
