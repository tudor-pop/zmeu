package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * BlockStatement
 * : '{' OptionalStatementList '}'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class WhileStatement extends Statement {
    private Expression test;
    private Statement body;

    public WhileStatement(Expression test, @Nullable Statement body) {
        this();
        this.test = test;
        this.body = body;
    }
    public WhileStatement(Expression test, @Nullable Expression body) {
        this(test, ExpressionStatement.of(body));
    }

    public WhileStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public static Statement of(Expression test, Expression consequent) {
        return new WhileStatement(test, consequent);
    }

    public static Statement of(Expression test, Statement consequent) {
        return new WhileStatement(test,consequent);
    }

    public static Statement of(Expression test, int value) {
        return new WhileStatement(test, NumericLiteral.of(value));
    }

    public static Statement of(Expression test, double value) {
        return new WhileStatement(test,NumericLiteral.of(value));
    }

    public static Statement of() {
        return new WhileStatement();
    }

    public static Statement of(Expression test, float value) {
        return new WhileStatement(test,NumericLiteral.of(value));
    }

    public static Statement of(Expression test, String value) {
        return new WhileStatement(test, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}