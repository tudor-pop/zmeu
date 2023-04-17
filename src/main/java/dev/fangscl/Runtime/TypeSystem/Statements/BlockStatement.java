package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * BlockStatement
 * : '{' OptionalStatementList '}'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockStatement extends Statement {
    private List<Statement> expression;

    public BlockStatement(@Nullable Statement... expression) {
        this.kind = NodeType.BlockStatement;
        this.expression = List.of(expression);
    }

    public BlockStatement(@Nullable List<Statement> expression) {
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

    public static Statement of(Statement... expression) {
        return new BlockStatement(expression);
    }

    public static Statement of(List<Statement> expression) {
        return new BlockStatement(expression);
    }

    public static Statement of(int value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of(double value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of() {
        return new BlockStatement(Collections.emptyList());
    }

    public static Statement of(float value) {
        return new BlockStatement(Literal.of(value));
    }

    public static Statement of(String value) {
        return new BlockStatement(Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return expression.stream().map(Statement::toSExpression).collect(Collectors.joining());
    }
}
