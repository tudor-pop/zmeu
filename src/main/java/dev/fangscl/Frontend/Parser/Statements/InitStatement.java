package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
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
public class InitStatement extends Statement {
    private Identifier name = Identifier.of("init");
    private List<Expression> params;
    private Statement body;

    public InitStatement(List<Expression> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.body = body;
    }

    public InitStatement() {
        this.kind = NodeType.InitDeclaration;
    }

    public static Statement of(List<Expression> params, Expression body) {
        return new InitStatement(params, body);
    }

    public static Statement of(List<Expression> params, Statement body) {
        return new InitStatement(params, body);
    }

    public static Statement of(List<Expression> params, int value) {
        return new InitStatement(params, NumericLiteral.of(value));
    }

    public static Statement of(List<Expression> params, double value) {
        return new InitStatement(params, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new InitStatement();
    }

    public static Statement of(List<Expression> params, float value) {
        return new InitStatement(params, NumericLiteral.of(value));
    }

    public static Statement of(List<Expression> params, String value) {
        return new InitStatement(params, StringLiteral.of(value));
    }

    @Override
    public String toSExpression() {
        return name.toSExpression() + body.toSExpression();
    }
}
