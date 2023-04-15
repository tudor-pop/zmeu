package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
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
public class IfStatement extends Statement {
    private Expression test;
    private Statement consequent;
    private Statement alternate;

    public IfStatement(@Nullable Statement consequent, Statement alternate) {
        this.kind = NodeType.IfStatement;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public IfStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public static Statement of(Expression test, Expression consequent, Expression alternate) {
        return new IfStatement(consequent, alternate);
    }

    public static Statement of(Expression test, Statement consequent, Statement alternate) {
        return new IfStatement(consequent, alternate);
    }

    public static Statement of(Expression test, int value) {
        return new IfStatement(Literal.of(value), null);
    }

    public static Statement of(Expression test, double value) {
        return new IfStatement(Literal.of(value), null);
    }

    public static Statement of() {
        return new IfStatement();
    }

    public static Statement of(Expression test, float value) {
        return new IfStatement(Literal.of(value), null);
    }

    public static Statement of(Expression test, String value) {
        return new IfStatement(Literal.of(value), null);
    }

    @Override
    public String toSExpression() {
        return test.toSExpression() + consequent.toSExpression();
    }
}
