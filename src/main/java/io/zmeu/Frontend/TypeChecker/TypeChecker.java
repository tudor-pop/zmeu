package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.Frontend.visitors.LanguageAstPrinter;
import io.zmeu.types.Types;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TypeChecker implements Visitor<Types> {
    private final LanguageAstPrinter printer = new LanguageAstPrinter();

    public TypeChecker() {
    }

    @Override
    public Types eval(Expression expression) {
        try {
            return expression.accept(this);
        } catch (TypeError typeError) {
            log.error(typeError.getMessage());
            throw typeError;
        }
    }

    @Override
    public Types eval(NumberLiteral expression) {
        return Types.Number;
    }

    @Override
    public Types eval(BooleanLiteral expression) {
        return Types.Boolean;
    }

    @Override
    public Types eval(Identifier expression) {
        return null;
    }

    @Override
    public Types eval(NullLiteral expression) {
        return null;
    }

    @Override
    public Types eval(StringLiteral expression) {
        return Types.String;
    }

    @Override
    public Types eval(LambdaExpression expression) {
        return null;
    }

    @Override
    public Types eval(BlockExpression expression) {
        return null;
    }

    @Override
    public Types eval(GroupExpression expression) {
        return null;
    }

    @Override
    public Types eval(BinaryExpression expression) {
        var op = expression.getOperator();
        Expression right = expression.getRight();
        Expression left = expression.getLeft();
        if (left == null || right == null) {
            throw new TypeError("Operator " + op + " expects 2 arguments");
        }
        var t1 = eval(left);
        var t2 = eval(right);

        var allowedTypes = allowTypes(op);
        this.expectOperatorType(t1, allowedTypes, expression);
        this.expectOperatorType(t2, allowedTypes, expression);
        return expect(t1, t2, left, expression);
    }

    private void expectOperatorType(Types type, List<Types> allowedTypes, BinaryExpression expression) {
        if (!allowedTypes.contains(type)) {
            throw new TypeError("Unexpected type: " + type + " in expression " + printer.eval(expression) + ". Allowed types: " + allowedTypes);
        }
    }

    private List<Types> allowTypes(String op) {
        return switch (op) {
            case "+", "==" -> List.of(Types.Number, Types.String); // allow addition for numbers and string
            case "-", "/", "*", "%" -> List.of(Types.Number);
            default -> throw new TypeError("Unknown operator " + op);
        };
    }

    private Types expect(Types actualType, Types expectedType, Expression expectedVal, Expression actualVal) {
        if (actualType != expectedType) {
            // only evaluate printing if we need to
            String string = "Expected type " + expectedType + " for " + printer.eval(expectedVal) + " but got " + actualType + " in expression: " + printer.eval(actualVal);
            throw new TypeError(string);
        }
        return actualType;
    }

    @Override
    public Types eval(CallExpression<Expression> expression) {
        return null;
    }

    @Override
    public Types eval(ErrorExpression expression) {
        return null;
    }

    @Override
    public Types eval(LogicalExpression expression) {
        return null;
    }

    @Override
    public Types eval(MemberExpression expression) {
        return null;
    }

    @Override
    public Types eval(ThisExpression expression) {
        return null;
    }

    @Override
    public Types eval(UnaryExpression expression) {
        return null;
    }

    @Override
    public Types eval(VariableDeclaration expression) {
        return null;
    }

    @Override
    public Types eval(AssignmentExpression expression) {
        return null;
    }

    @Override
    public Types eval(float expression) {
        return null;
    }

    @Override
    public Types eval(double expression) {
        return null;
    }

    @Override
    public Types eval(int expression) {
        return null;
    }

    @Override
    public Types eval(boolean expression) {
        return null;
    }

    @Override
    public Types eval(String expression) {
        return null;
    }
}
