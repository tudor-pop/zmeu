package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
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
    private List<Identifier> params;
    private Statement body;

    private LambdaExpression(List<Identifier> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.body = body;
    }

    private LambdaExpression(List<Identifier> params, @Nullable Expression body) {
        this(params, ExpressionStatement.expressionStatement(body));
    }

    private LambdaExpression() {
        this.kind = NodeType.LambdaExpression;
    }

    public static Expression lambda(List<Identifier> params, Expression body) {
        return new LambdaExpression(params, body);
    }

    public static Expression lambda(Identifier params, Expression body) {
        return new LambdaExpression(List.of(params), body);
    }

    public static Expression lambda(Identifier p1, Identifier p2, Expression body) {
        return new LambdaExpression(List.of(p1, p2), body);
    }

    public static Expression lambda(String params, Expression body) {
        return new LambdaExpression(List.of(TypeIdentifier.type(params)), body);
    }

    public static Expression lambda(String p1, String p2, Expression body) {
        return new LambdaExpression(List.of(TypeIdentifier.type(p1), TypeIdentifier.type(p2)), body);
    }

    public static Expression lambda(List<Identifier> params, Statement body) {
        return new LambdaExpression(params, body);
    }

    public static Expression lambda(List<Identifier> params, int value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda(List<Identifier> params, double value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda() {
        return new LambdaExpression();
    }

    public static Expression lambda(List<Identifier> params, float value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression lambda(List<Identifier> params, String value) {
        return new LambdaExpression(params, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
