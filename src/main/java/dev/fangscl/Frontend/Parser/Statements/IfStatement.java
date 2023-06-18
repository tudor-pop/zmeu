package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
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

    private IfStatement(Expression test, @Nullable Statement consequent, Statement alternate) {
        this.kind = NodeType.IfStatement;
        this.test = test;
        this.consequent = consequent;
        this.alternate = alternate;
    }
    private IfStatement(Expression test, @Nullable Expression consequent, Expression alternate) {
        this(test, ExpressionStatement.of(consequent), ExpressionStatement.of(alternate));
    }

    private IfStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public boolean hasElse() {
        return alternate != null;
    }

    public static Statement of(Expression test, Expression consequent, Expression alternate) {
        return new IfStatement(test, consequent, alternate);
    }

    public static Statement of(Expression test, Statement consequent, Statement alternate) {
        return new IfStatement(test,consequent, alternate);
    }
    public static Statement of(Expression test, Statement consequent) {
        return IfStatement.of(test,consequent, null);
    }

    public static Statement of(Expression test, int value) {
        return new IfStatement(test, NumericLiteral.of(value), null);
    }

    public static Statement of(Expression test, double value) {
        return new IfStatement(test,NumericLiteral.of(value), null);
    }

    public static Statement of() {
        return new IfStatement();
    }

    public static Statement of(Expression test, float value) {
        return new IfStatement(test,NumericLiteral.of(value), null);
    }

    public static Statement of(Expression test, String value) {
        return new IfStatement(test, StringLiteral.of(value), null);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
