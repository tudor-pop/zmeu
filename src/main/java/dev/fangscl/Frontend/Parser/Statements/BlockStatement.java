package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BlockStatement
 * : { Statements? }
 * ;
 * Statements
 * : Statement* Expression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockStatement extends Expression {
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

    public static Expression of(Expression expression) {
        return new BlockStatement(expression);
    }

    public static BlockStatement of(Statement expression) {
        return new BlockStatement(expression);
    }

    public static BlockStatement of(Statement... expression) {
        return new BlockStatement(expression);
    }

    public static Expression of(List<Statement> expression) {
        return new BlockStatement(expression);
    }

    public static Expression of(int value) {
        return new BlockStatement(NumericLiteral.of(value));
    }

    public static Expression of(double value) {
        return new BlockStatement(NumericLiteral.of(value));
    }

    public static Expression of() {
        return new BlockStatement(Collections.emptyList());
    }

    public static Expression of(float value) {
        return new BlockStatement(NumericLiteral.of(value));
    }

    public static Expression of(String value) {
        return new BlockStatement(StringLiteral.of(value));
    }

    @Override
    public String toSExpression() {
        return expression.stream().map(Statement::toSExpression).collect(Collectors.joining());
    }
}
