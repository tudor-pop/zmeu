package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThisExpression extends Expression {
    private Identifier instance;

    private ThisExpression() {
        this.kind = NodeType.ThisExpression;
    }

    private ThisExpression(Identifier instance) {
        this();
        this.instance = instance;
    }

    public static Expression of(Identifier operator) {
        return new ThisExpression(operator);
    }
    public static Expression of() {
        return new ThisExpression();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

}
