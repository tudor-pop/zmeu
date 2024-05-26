package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
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
        this(params,ExpressionStatement.of(body));
    }

    private LambdaExpression() {
        this.kind = NodeType.LambdaExpression;
    }

    public static Expression of(List<Identifier> params, Expression body) {
        return new LambdaExpression(params, body);
    }

    public static Expression of(List<Identifier> params, Statement body) {
        return new LambdaExpression(params, body);
    }

    public static Expression of(List<Identifier> params, int value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression of(List<Identifier> params, double value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression of() {
        return new LambdaExpression();
    }

    public static Expression of(List<Identifier> params, float value) {
        return new LambdaExpression(params, NumberLiteral.of(value));
    }

    public static Expression of(List<Identifier> params, String value) {
        return new LambdaExpression(params, StringLiteral.of(value));
    }


    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
