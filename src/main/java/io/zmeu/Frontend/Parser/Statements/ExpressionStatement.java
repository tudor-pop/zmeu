package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An expression statement is one that evaluates an expression and ignores its result.
 * As a rule, an expression statement's purpose is to trigger the effects of evaluating its expression.
 * ExpressionStatement
 * : Expression '\n'
 * | Statement
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionStatement extends Statement {
    private Expression statement;

    private ExpressionStatement(Expression statement) {
        this.kind = NodeType.ExpressionStatement;
        this.statement = statement;
    }

    private ExpressionStatement() {
        this(null);
    }

    public static Statement expressionStatement(Expression expression) {
        return new ExpressionStatement(expression);
    }

    public static Statement expressionStatement() {
        return new ExpressionStatement();
    }

    public static Statement expressionStatement(int value) {
        return new ExpressionStatement(NumberLiteral.of(value));
    }

    public static Statement expressionStatement(double value) {
        return new ExpressionStatement(NumberLiteral.of(value));
    }

    public static Statement expressionStatement(float value) {
        return new ExpressionStatement(NumberLiteral.of(value));
    }

    public static Statement expressionStatement(String value) {
        return new ExpressionStatement(StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
