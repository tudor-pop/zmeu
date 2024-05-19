package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.Frontend.visitors.LanguageAstPrinter;
import io.zmeu.types.Types;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeChecker implements Visitor<Types> {
    private LanguageAstPrinter printer = new LanguageAstPrinter();

    public TypeChecker() {
    }

    @Override
    public Types eval(Expression expression) {
        return expression.accept(this);
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
            throw new TypeError("\nOperator " + op + " expects 2 arguments");
        }
        var t1 = eval(left);
        var t2 = eval(right);
        return expect(t1, t2, printer.eval(left), printer.eval(expression));
    }

    private static Types expect(Types actualType, Types expectedType, Object expectedVal, Object actualVal) {
        if (actualType != expectedType) {
            String string = "\nExpected '" + expectedType + "' for " + expectedVal + " but got '" + actualType + "' in expression " + actualVal;
            log.error(string);
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
