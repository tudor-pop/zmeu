package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ThisExpression extends Expression {
    private Identifier instance;

    private ThisExpression() {
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
        return visitor.eval(this);
    }

}
