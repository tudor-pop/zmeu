package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
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
    private List<Expression> params;
    private Statement body;

    private LambdaExpression(List<Expression> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.body = body;
    }

    private LambdaExpression(List<Expression> params, @Nullable Expression body) {
        this(params,ExpressionStatement.of(body));
    }

    private LambdaExpression() {
        this.kind = NodeType.LambdaExpression;
    }

    public static Expression of(List<Expression> params, Expression body) {
        return new LambdaExpression(params, body);
    }

    public static Expression of(List<Expression> params, Statement body) {
        return new LambdaExpression(params, body);
    }

    public static Expression of(List<Expression> params, int value) {
        return new LambdaExpression(params, NumericLiteral.of(value));
    }

    public static Expression of(List<Expression> params, double value) {
        return new LambdaExpression(params, NumericLiteral.of(value));
    }

    public static Expression of() {
        return new LambdaExpression();
    }

    public static Expression of(List<Expression> params, float value) {
        return new LambdaExpression(params, NumericLiteral.of(value));
    }

    public static Expression of(List<Expression> params, String value) {
        return new LambdaExpression(params, StringLiteral.of(value));
    }


    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
