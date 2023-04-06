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
public class BlockStatement extends Statement {
    private Statement expression;

    public BlockStatement(@Nullable Statement expression) {
        this.kind = NodeType.BlockStatement;
        this.expression = expression;
    }

    public BlockStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public static Statement of(Expression expression) {
        return new BlockStatement(expression);
    }

    public static Statement of(Statement expression) {
        return new BlockStatement(expression);
    }

    public static Statement of(int value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of(double value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of() {
        return new BlockStatement();
    }

    public static Statement of(float value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of(String value) {
        return new BlockStatement(Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return  expression.toSExpression() ;
    }
}
