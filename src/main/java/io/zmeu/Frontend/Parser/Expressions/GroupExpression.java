package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class GroupExpression extends Expression {
    private Expression expression;

    public GroupExpression(Expression expression) {
        this.expression = expression;
        this.kind = NodeType.LogicalExpression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
