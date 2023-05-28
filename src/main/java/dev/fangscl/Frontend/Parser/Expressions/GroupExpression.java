package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupExpression extends Expression {
    private Expression expression;

    public GroupExpression(Expression expression) {
        this.expression = expression;
        this.kind = NodeType.LogicalExpression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

}
