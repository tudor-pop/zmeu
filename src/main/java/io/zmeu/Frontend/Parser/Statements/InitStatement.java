package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
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
public class InitStatement extends Statement {
    private Identifier name = Identifier.of("init");
    private List<Identifier> params;
    private Statement body;

    private InitStatement(List<Identifier> params, @Nullable Statement body) {
        this();
        this.params = params;
        this.body = body;
    }

    private InitStatement(List<Identifier> params, @Nullable Expression body) {
        this(params, ExpressionStatement.of(body));
    }

    public InitStatement() {
        this.kind = NodeType.InitDeclaration;
    }

    public static Statement of(List<Identifier> params, Expression body) {
        return new InitStatement(params, body);
    }

    public static Statement of(List<Identifier> params, Statement body) {
        return new InitStatement(params, body);
    }

    public static Statement of() {
        return new InitStatement();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
