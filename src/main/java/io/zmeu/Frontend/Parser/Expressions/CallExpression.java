package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Visitors.Visitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public final class CallExpression<E extends Expression> extends Expression {
    private Expression callee;
    private List<E> arguments;

    public CallExpression() {
        this.kind = NodeType.CallExpression;
        arguments = Collections.emptyList();
    }

    private CallExpression(Expression callee, List<E> arguments) {
        this();
        this.callee = callee;
        this.arguments = arguments;
    }

    public static Expression call(Expression callee, int arguments) {
        return new CallExpression(callee, List.of(NumberLiteral.of(arguments)));
    }

    public static Expression call(Expression callee, double arguments) {
        return new CallExpression(callee, List.of(NumberLiteral.of(arguments)));
    }

    public static Expression call(Expression callee, float arguments) {
        return new CallExpression(callee, List.of(NumberLiteral.of(arguments)));
    }

    public static Expression call(Expression callee, String arguments) {
        return new CallExpression(callee, List.of(Identifier.id(arguments)));
    }
    public static Expression call(Expression callee, StringLiteral arguments) {
        return new CallExpression(callee, List.of(arguments));
    }

    public static Expression call(String callee, int arguments) {
        return new CallExpression(Identifier.id(callee), List.of(NumberLiteral.of(arguments)));
    }
    public static Expression call(String callee, double arguments) {
        return new CallExpression(Identifier.id(callee), List.of(NumberLiteral.of(arguments)));
    }

    public static Expression call(int callee, int arguments) {
        return new CallExpression(NumberLiteral.of(callee), List.of(NumberLiteral.of(arguments)));
    }

    public static Expression call(String callee, String... arguments) {
        return new CallExpression(Identifier.id(callee), Identifier.id(arguments));
    }

    public static <E extends Expression> Expression call(Expression callee, List<E> arguments) {
        return new CallExpression(callee, arguments);
    }


    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval((CallExpression<Expression>) this);
    }

}
