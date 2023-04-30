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
public class FunctionDeclarationStatement extends Statement {
    private Identifier name;
    private List<Expression> params;
    private Statement body;

    public FunctionDeclarationStatement(Identifier name, List<Expression> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.name = name;
        this.body = body;
    }

    public FunctionDeclarationStatement() {
        this.kind = NodeType.FunctionDeclaration;
    }

    public static Statement of(Identifier test, List<Expression> params, Expression body) {
        return new FunctionDeclarationStatement(test, params, body);
    }

    public static Statement of(Identifier test, List<Expression> params, Statement body) {
        return new FunctionDeclarationStatement(test, params, body);
    }

    public static Statement of(Identifier test, List<Expression> params, int value) {
        return new FunctionDeclarationStatement(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Expression> params, double value) {
        return new FunctionDeclarationStatement(test, params, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new FunctionDeclarationStatement();
    }

    public static Statement of(Identifier test, List<Expression> params, float value) {
        return new FunctionDeclarationStatement(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Expression> params, String value) {
        return new FunctionDeclarationStatement(test, params, StringLiteral.of(value));
    }

    @Override
    public String toSExpression() {
        return name.toSExpression() + body.toSExpression();
    }
}
