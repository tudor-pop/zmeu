package dev.fangscl.Runtime;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Literals.DecimalLiteral;
import dev.fangscl.Runtime.TypeSystem.Literals.Identifier;
import dev.fangscl.Runtime.TypeSystem.Literals.IntegerLiteral;
import dev.fangscl.Runtime.TypeSystem.Literals.StringLiteral;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

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

    public RuntimeValue eval(List<Object> arg, Environment environment) {
        RuntimeValue lastEval = new NullValue();
        for (Object i : arg) {
            lastEval = eval(i, environment);
        }
        return lastEval;
    }

    public RuntimeValue eval(Object arg, Environment environment) {
        if (arg instanceof Program program) {
            return eval(program, environment);
        } else if (arg instanceof Statement statement) {
            return eval(statement, environment);
        } else if (arg instanceof Integer number) {
            return eval(number);
        } else if (arg instanceof Double number) {
            return eval(number);
        } else if (arg instanceof Float number) {
            return eval(number);
        } else if (arg instanceof String str) {
            return eval(str);
        }

        throw new RuntimeException("C");
    }

    public RuntimeValue eval(Statement expression) {
        return this.eval(expression, this.global);
    }

    public RuntimeValue eval(Statement statement, Environment env) {
        if (statement instanceof Program program) {
            return eval(program, env);
        } else if (statement instanceof Expression expression) {
            return eval(expression, env);
        }
        throw new RuntimeException("error");
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

    public RuntimeValue eval(Expression expression, Environment env) {
        if (expression instanceof IntegerLiteral e) {
            return new IntegerValue(e);
        } else if (expression instanceof DecimalLiteral e) {
            return new DecimalValue(e);
        } else if (expression instanceof StringLiteral e) {
            return new StringValue(e);
        } else if (expression instanceof BinaryExpression e) {
            return eval(e, env);
        } else if (expression instanceof Identifier e) {
            return env.evaluateVar(e.getSymbol());
        } else {
            log.debug("This AST does cannot be interpreted yet {}", expression);
            System.exit(1);
        }
        return this.eval(expression, this.global);
    }

    private RuntimeValue eval(BinaryExpression expression, Environment environment) {
        var lhs = eval(expression.getLeft(), environment);
        var rhs = eval(expression.getRight(), environment);
        if ((lhs.getType() == ValueType.Decimal && rhs.getType() == ValueType.Decimal)) {
            return eval(lhs, rhs, expression.getOperator());
        } else if (lhs.getType() == ValueType.Integer && rhs.getType() == ValueType.Integer) {
            return eval(lhs, rhs, expression.getOperator());
        } else if (lhs.getType() == ValueType.Integer && rhs.getType() == ValueType.Decimal) {
            return eval(lhs, rhs, expression.getOperator());
        } else if (lhs.getType() == ValueType.Decimal && rhs.getType() == ValueType.Integer) {
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
