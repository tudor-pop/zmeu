package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
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
public class ReturnStatement extends Statement {
    private Identifier name;
    private Statement argument;

    public ReturnStatement(@Nullable Statement argument) {
        this();
        this.argument = argument;
    }
    public ReturnStatement(@Nullable Expression argument) {
        this(ExpressionStatement.of(argument));
    }

    public ReturnStatement() {
        this.kind = NodeType.ReturnStatement;
    }

    public static Statement of(Expression body) {
        return new ReturnStatement(body);
    }

    public static Statement of(Statement body) {
        return new ReturnStatement(body);
    }

    public static Statement of(int value) {
        return new ReturnStatement(NumericLiteral.of(value));
    }

    public static Statement of(double value) {
        return new ReturnStatement(NumericLiteral.of(value));
    }

    public static Statement of() {
        return new ReturnStatement();
    }

    public static Statement of(float value) {
        return new ReturnStatement(NumericLiteral.of(value));
    }

    public static Statement of(String value) {
        return new ReturnStatement(StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
