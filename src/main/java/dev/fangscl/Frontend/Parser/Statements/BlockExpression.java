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
 * BlockExpression
 * : { Statements? }
 * ;
 * Statements
 * : Statement* Expression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockExpression extends Expression {
    private List<Statement> expression;

    public BlockExpression(@Nullable Statement... expression) {
        this.kind = NodeType.BlockExpression;
        this.expression = List.of(expression);
    }

    public BlockExpression(@Nullable List<Statement> expression) {
        this.kind = NodeType.BlockExpression;
        this.expression = expression;
    }

    public BlockExpression() {
        this.kind = NodeType.BlockExpression;
    }

    public static Expression of(Expression expression) {
        return new BlockExpression(expression);
    }

    public static Expression of(Statement expression) {
        return new BlockExpression(expression);
    }

    public static Expression of(Statement... expression) {
        return new BlockExpression(expression);
    }

    public static Expression of(List<Statement> expression) {
        return new BlockExpression(expression);
    }

    public static Expression of(int value) {
        return new BlockExpression(NumericLiteral.of(value));
    }

    public static Expression of(double value) {
        return new BlockExpression(NumericLiteral.of(value));
    }

    public static Expression of() {
        return new BlockExpression(Collections.emptyList());
    }

    public static Expression of(float value) {
        return new BlockExpression(NumericLiteral.of(value));
    }

    public static Expression of(String value) {
        return new BlockExpression(StringLiteral.of(value));
    }

    @Override
    public String toSExpression() {
        return expression.stream().map(Statement::toSExpression).collect(Collectors.joining());
    }
}
