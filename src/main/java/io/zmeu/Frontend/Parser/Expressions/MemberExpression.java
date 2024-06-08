package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class MemberExpression extends Expression {
    private boolean computed;
    private Expression object;
    private Expression property;

    public MemberExpression() {
        this.kind = NodeType.MemberExpression;
    }

    private MemberExpression(boolean computed, Expression object, Expression property) {
        this();
        this.computed = computed;
        this.object = object;
        this.property = property;
    }

    public static Expression member(boolean computed, Expression object, Expression property) {
        return new MemberExpression(computed, object, property);
    }
    public static Expression member(boolean computed, Expression object, int property) {
        return new MemberExpression(computed, object, NumberLiteral.of(property));
    }
    public static Expression member(boolean computed, Expression object, String property) {
        return new MemberExpression(computed, object, Identifier.of(property));
    }
    public static Expression member(Expression object, String property) {
        return new MemberExpression(false, object, Identifier.of(property));
    }
    public static Expression member(boolean computed, String object, int property) {
        return new MemberExpression(computed, Identifier.of(object), NumberLiteral.of(property));
    }

    public static Expression member(boolean computed, int object, int property) {
        return new MemberExpression(computed, NumberLiteral.of(object), NumberLiteral.of(property));
    }

    public static Expression member(boolean computed, String object, String property) {
        return new MemberExpression(computed, Identifier.of(object), Identifier.of(property));
    }

    public static Expression member(String object, String property) {
        return new MemberExpression(false, Identifier.of(object), Identifier.of(property));
    }

    public static Expression member(Expression object, Expression property) {
        return new MemberExpression(false, object, property);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
