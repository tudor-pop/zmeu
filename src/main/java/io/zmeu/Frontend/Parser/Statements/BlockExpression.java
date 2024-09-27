package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Visitors.Visitor;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * BlockStatement
 * : { Statements? }
 * ;
 * Statements
 * : Statement* Expression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public non-sealed class BlockExpression extends Expression {
    private List<Statement> expression;

    public BlockExpression(@Nullable Statement... expression) {
        this.kind = NodeType.BlockStatement;
        this.expression = List.of(expression);
    }

    public BlockExpression(@Nullable List<Statement> expression) {
        this.kind = NodeType.BlockStatement;
        this.expression = expression;
    }
    public BlockExpression(@Nullable Expression expression) {
        this(ExpressionStatement.expressionStatement(expression));
    }

    public BlockExpression() {
        this.kind = NodeType.BlockStatement;
    }

    public static Expression block(Expression expression) {
        return new BlockExpression(expression);
    }


    public static BlockExpression block(Statement expression) {
        return new BlockExpression(expression);
    }

    public static BlockExpression block(Statement... expression) {
        return new BlockExpression(expression);
    }

    public static Expression block(List<Statement> expression) {
        return new BlockExpression(expression);
    }

    public static Expression block(int value) {
        return new BlockExpression(NumberLiteral.of(value));
    }

    public static Expression block(double value) {
        return new BlockExpression(NumberLiteral.of(value));
    }

    public static Expression block() {
        return new BlockExpression(Collections.emptyList());
    }

    public static Expression block(float value) {
        return new BlockExpression(NumberLiteral.of(value));
    }

    public static Expression block(String value) {
        return new BlockExpression(StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
