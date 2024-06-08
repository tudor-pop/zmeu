package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.visitors.Visitor;
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

    public static Statement fun(Identifier test, List<Identifier> params, Expression body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement fun(String test, List<Identifier> params, Expression body) {
        return FunctionDeclaration.fun(Identifier.of(test), params, body);
    }
    public static Statement fun(String test, Expression body) {
        return FunctionDeclaration.fun(Identifier.of(test), List.of(), body);
    }

    public static Statement fun(Identifier test, List<Identifier> params, Statement body) {
        return new FunctionDeclaration(test, params, body);
    }

    public static Statement fun(Identifier test, List<Identifier> params, int value) {
        return FunctionDeclaration.fun(test, params, NumberLiteral.of(value));
    }

    public static Statement fun(Identifier test, List<Identifier> params, double value) {
        return FunctionDeclaration.fun(test, params, NumberLiteral.of(value));
    }

    public static Statement fun() {
        return new FunctionDeclaration();
    }

    public static Statement fun(Identifier test, List<Identifier> params, float value) {
        return FunctionDeclaration.fun(test, params, NumberLiteral.of(value));
    }

    public static Statement fun(Identifier test, List<Identifier> params, String value) {
        return FunctionDeclaration.fun(test, params, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
