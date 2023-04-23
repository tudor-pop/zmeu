package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
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
        this.kind = NodeType.WhileStatement;
        this.test = test;
        this.body = body;
    }

    public WhileStatement() {
        this.kind = NodeType.BlockExpression;
    }

    public static Statement of(Expression test, Expression consequent) {
        return new WhileStatement(test,consequent);
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
    public String toSExpression() {
        return test.toSExpression() + body.toSExpression();
    }
}
