package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
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
        this.kind = NodeType.IfStatement;
        this.test = test;
        this.body = body;
    }

    public WhileStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public static Statement of(Expression test, Expression consequent) {
        return new WhileStatement(test,consequent);
    }

    public static Statement of(Expression test, Statement consequent) {
        return new WhileStatement(test,consequent);
    }

    public static Statement of(Expression test, int value) {
        return new WhileStatement(test,Literal.of(value));
    }

    public static Statement of(Expression test, double value) {
        return new WhileStatement(test,Literal.of(value));
    }

    public static Statement of() {
        return new WhileStatement();
    }

    public static Statement of(Expression test, float value) {
        return new WhileStatement(test,Literal.of(value));
    }

    public static Statement of(Expression test, String value) {
        return new WhileStatement(test,Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return test.toSExpression() + body.toSExpression();
    }
}
