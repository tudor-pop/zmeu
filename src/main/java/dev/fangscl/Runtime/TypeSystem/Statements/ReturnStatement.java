package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
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
public class ReturnStatement extends Statement {
    private Identifier name;
    private Statement argument;

    public ReturnStatement(@Nullable Statement argument) {
        this();
        this.argument = argument;
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
        return new ReturnStatement(Literal.of(value));
    }

    public static Statement of(double value) {
        return new ReturnStatement(Literal.of(value));
    }

    public static Statement of() {
        return new ReturnStatement();
    }

    public static Statement of(float value) {
        return new ReturnStatement(Literal.of(value));
    }

    public static Statement of(String value) {
        return new ReturnStatement(Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return name.toSExpression() + argument.toSExpression();
    }
}
