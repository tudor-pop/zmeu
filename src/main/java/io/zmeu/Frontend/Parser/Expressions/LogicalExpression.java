package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LogicalExpression extends Expression {
    private Expression left;
    private Expression right;
    private TokenType operator;

    public LogicalExpression() {
        this.kind = NodeType.LogicalExpression;
    }

    public LogicalExpression(Expression left, Expression right, TokenType operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static Expression of(Object operator, Expression left, Expression right) {
        return new LogicalExpression(left, right, TokenType.toSymbol(operator.toString()));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
