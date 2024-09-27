package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
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
        this(test, ExpressionStatement.expressionStatement(consequent), ExpressionStatement.expressionStatement(alternate));
    }

    private IfStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public boolean hasElse() {
        return alternate != null;
    }

    public static Statement If(Expression test, Expression consequent, Expression alternate) {
        return new IfStatement(test, consequent, alternate);
    }

    public static Statement If(Expression test, Statement consequent, Statement alternate) {
        return new IfStatement(test,consequent, alternate);
    }
    public static Statement If(Expression test, Statement consequent) {
        return IfStatement.If(test,consequent, null);
    }

    public static Statement If(Expression test, int value) {
        return new IfStatement(test, NumberLiteral.of(value), null);
    }

    public static Statement If(Expression test, double value) {
        return new IfStatement(test, NumberLiteral.of(value), null);
    }

    public static Statement If() {
        return new IfStatement();
    }

    public static Statement If(Expression test, float value) {
        return new IfStatement(test, NumberLiteral.of(value), null);
    }

    public static Statement If(Expression test, String value) {
        return new IfStatement(test, StringLiteral.of(value), null);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
