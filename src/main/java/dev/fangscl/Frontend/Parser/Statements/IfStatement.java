package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
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

    public IfStatement(Expression test, @Nullable Statement consequent, Statement alternate) {
        this.kind = NodeType.IfStatement;
        this.test = test;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public IfStatement() {
        this.kind = NodeType.BlockExpression;
    }

    public static Statement of(Expression test, Expression consequent, Expression alternate) {
        return new IfStatement(test,consequent, alternate);
    }

    public static Statement of(Expression test, Statement consequent, Statement alternate) {
        return new IfStatement(test,consequent, alternate);
    }
    public static Statement of(Expression test, Statement consequent) {
        return IfStatement.of(test,consequent, null);
    }

    public static Statement of(Expression test, int value) {
        return new IfStatement(test,Literal.of(value), null);
    }

    public static Statement of(Expression test, double value) {
        return new IfStatement(test,Literal.of(value), null);
    }

    public static Statement of() {
        return new IfStatement();
    }

    public static Statement of(Expression test, float value) {
        return new IfStatement(test,Literal.of(value), null);
    }

    public static Statement of(Expression test, String value) {
        return new IfStatement(test, StringLiteral.of(value), null);
    }

    @Override
    public String toSExpression() {
        return test.toSExpression() + consequent.toSExpression();
    }
}
