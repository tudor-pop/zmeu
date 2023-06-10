package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class CallExpression<E extends Expression> extends Expression {
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

    public static Expression of(Expression callee, int arguments) {
        return new CallExpression(callee, List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(Expression callee, double arguments) {
        return new CallExpression(callee, List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(Expression callee, float arguments) {
        return new CallExpression(callee, List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(Expression callee, String arguments) {
        return new CallExpression(callee, List.of(Identifier.of(arguments)));
    }
    public static Expression of(Expression callee, StringLiteral arguments) {
        return new CallExpression(callee, List.of(arguments));
    }

    public static Expression of(String callee, int arguments) {
        return new CallExpression(Identifier.of(callee), List.of(NumericLiteral.of(arguments)));
    }
    public static Expression of(String callee, double arguments) {
        return new CallExpression(Identifier.of(callee), List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(int callee, int arguments) {
        return new CallExpression(NumericLiteral.of(callee), List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(String callee, String... arguments) {
        return new CallExpression(Identifier.of(callee), Identifier.of(arguments));
    }

    public static <E extends Expression> Expression of(Expression callee, List<E> arguments) {
        return new CallExpression(callee, arguments);
    }


    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval((CallExpression<Expression>) this);
    }

}
