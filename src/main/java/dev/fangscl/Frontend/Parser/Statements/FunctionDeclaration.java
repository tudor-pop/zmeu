package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * BlockStatement
 * : '{' OptionalStatementList '}'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionDeclaration extends Statement {
    private Identifier name;
    private List<Expression> params;
    private Statement body;

    public FunctionDeclaration(Identifier name, List<Expression> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.name = name;
        this.body = body;
    }

    public FunctionDeclaration() {
        this.kind = NodeType.FunctionDeclaration;
    }

    public static Statement of(Identifier test, List<Expression> params, Expression body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement of(String test, List<Expression> params, Expression body) {
        return FunctionDeclaration.of(Identifier.of(test), params, body);
    }

    public static Statement of(String test, Expression body) {
        return FunctionDeclaration.of(Identifier.of(test), Collections.emptyList(), body);
    }

    public static Statement of(String test) {
        return FunctionDeclaration.of(Identifier.of(test), Collections.emptyList(), BlockStatement.of());
    }

    public static Statement of(Identifier test, List<Expression> params, Statement body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement of(Identifier test, List<Expression> params, int value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Expression> params, double value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new FunctionDeclaration();
    }

    public static Statement of(Identifier test, List<Expression> params, float value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Expression> params, String value) {
        return FunctionDeclaration.of(test, params, StringLiteral.of(value));
    }

}
