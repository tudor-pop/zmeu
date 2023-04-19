package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
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
    private List<Identifier> params;
    private Statement body;

    public FunctionDeclarationStatement(Identifier name, List<Identifier> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.name = name;
        this.body = body;
    }

    public FunctionDeclarationStatement() {
        this.kind = NodeType.FunctionDeclarationStatement;
    }

    public static Statement of(Identifier test, List<Identifier> params, Expression body) {
        return new FunctionDeclarationStatement(test, params, body);
    }

    public static Statement of(Identifier test, List<Identifier> params, Statement body) {
        return new FunctionDeclarationStatement(test, params, body);
    }

    public static Statement of(Identifier test, List<Identifier> params, int value) {
        return new FunctionDeclarationStatement(test, params, Literal.of(value));
    }

    public static Statement of(Identifier test, List<Identifier> params, double value) {
        return new FunctionDeclarationStatement(test, params, Literal.of(value));
    }

    public static Statement of() {
        return new FunctionDeclarationStatement();
    }

    public static Statement of(Identifier test, List<Identifier> params, float value) {
        return new FunctionDeclarationStatement(test, params, Literal.of(value));
    }

    public static Statement of(Identifier test, List<Identifier> params, String value) {
        return new FunctionDeclarationStatement(test, params, Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return name.toSExpression() + body.toSExpression();
    }
}
