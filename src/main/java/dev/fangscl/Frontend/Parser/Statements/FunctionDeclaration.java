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
public class FunctionDeclaration extends Statement {
    private Identifier name;
    private List<Identifier> params;
    private Statement body;

    private FunctionDeclaration(Identifier name, List<Identifier> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.name = name;
        this.body = body;
    }

    private FunctionDeclaration(Identifier name, List<Identifier> params, @Nullable Expression body) {
        this(name,params,ExpressionStatement.of(body));
    }

    private FunctionDeclaration() {
        this.kind = NodeType.FunctionDeclaration;
    }

    public static Statement of(Identifier test, List<Identifier> params, Expression body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement of(String test, List<Identifier> params, Expression body) {
        return FunctionDeclaration.of(Identifier.of(test), params, body);
    }

    public static Statement of(Identifier test, List<Identifier> params, Statement body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement of(Identifier test, List<Identifier> params, int value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Identifier> params, double value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new FunctionDeclaration();
    }

    public static Statement of(Identifier test, List<Identifier> params, float value) {
        return FunctionDeclaration.of(test, params, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, List<Identifier> params, String value) {
        return FunctionDeclaration.of(test, params, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
