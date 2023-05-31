package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
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

    public static Statement of(Expression expression) {
        return new ExpressionStatement(expression);
    }
    public static Statement of() {
        return new ExpressionStatement();
    }
    public static Statement of(int value) {
        return new ExpressionStatement(NumericLiteral.of(value));
    }

    public static Statement of(double value) {
        return new ExpressionStatement(NumericLiteral.of(value));
    }

    public static Statement of(float value) {
        return new ExpressionStatement(NumericLiteral.of(value));
    }

    public static Statement of(String value) {
        return new ExpressionStatement(StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
