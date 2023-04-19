package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
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

    public static Expression of(Expression object, int arguments) {
        return new CallExpression(object, java.util.List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(Expression object, String arguments) {
        return new CallExpression(object, java.util.List.of(Identifier.of(arguments)));
    }

    public static Expression of(String object, int arguments) {
        return new CallExpression(Identifier.of(object), java.util.List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(int object, int arguments) {
        return new CallExpression(NumericLiteral.of(object), java.util.List.of(NumericLiteral.of(arguments)));
    }

    public static Expression of(String object, String... arguments) {
        return new CallExpression(Identifier.of(object), Identifier.of(arguments));
    }

    public static <E extends Expression> Expression of(Expression object, List<E> arguments) {
        return new CallExpression(object, arguments);
    }


    @Override
    public String toSExpression() {
        return "(" + callee.toSExpression() + " " +
               arguments.stream()
                       .map(Statement::toSExpression)
                       .collect(Collectors.joining()) + ")";
    }
}
