package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <p>
 * BlockStatement
 * : '{' OptionalStatementList '}'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LambdaExpression extends Expression {
    private List<ParameterIdentifier> params;
    private Statement body;

    private LambdaExpression(List<ParameterIdentifier> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.body = body;
    }

    private LambdaExpression(List<ParameterIdentifier> params, @Nullable Expression body) {
        this(params, ExpressionStatement.expressionStatement(body));
    }

    private LambdaExpression() {
        this.kind = NodeType.LambdaExpression;
    }

    public static Expression lambda(List<ParameterIdentifier> params, Expression body) {
        return new LambdaExpression(params, body);
    }

    public static Expression lambda(ParameterIdentifier params, Expression body) {
        return new LambdaExpression(List.of(params), body);
    }

    public static Expression lambda(ParameterIdentifier p1, ParameterIdentifier p2, Expression body) {
        return new LambdaExpression(List.of(p1, p2), body);
    }

    public static Expression lambda(String params, Expression body) {
        return new LambdaExpression(List.of(ParameterIdentifier.param(params)), body);
    }

    public static Expression lambda(String p1, String p2, Expression body) {
        return new LambdaExpression(List.of(ParameterIdentifier.param(p1), ParameterIdentifier.param(p2)), body);
    }

    public static Expression lambda(List<ParameterIdentifier> params, Statement body) {
        return new LambdaExpression(params, body);
    }

    public static Expression lambda(List<ParameterIdentifier> params, int value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda(List<ParameterIdentifier> params, double value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda() {
        return new LambdaExpression();
    }

    public static Expression lambda(List<ParameterIdentifier> params, float value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda(List<ParameterIdentifier> params, String value) {
        return new LambdaExpression(params, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
