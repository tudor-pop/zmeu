package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.PathIdentifier;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Statements.VariableStatement;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VariableDeclaration extends Expression {
    private Identifier id;
    private Expression init;
    private PathIdentifier type;

    public VariableDeclaration() {
        this.kind = NodeType.VariableDeclaration;
    }

    private VariableDeclaration(Expression id, Expression init) {
        this();
        this.id = (Identifier) id;
        this.init = init;
    }

    private VariableDeclaration(Expression id, PathIdentifier type) {
        this();
        this.id = (Identifier) id;
        this.type = type;
    }

    private VariableDeclaration(Expression id, PathIdentifier type, Expression init) {
        this();
        this.id = (Identifier) id;
        this.init = init;
        this.type = type;
    }

    private VariableDeclaration(Expression id) {
        this(id, null);
    }

    public static VariableDeclaration of(Expression id, Expression init) {
        return new VariableDeclaration(id, init);
    }

    public static VariableDeclaration var(Expression id, Expression init) {
        return new VariableDeclaration(id, init);
    }

    public static VariableDeclaration var(String id, Expression init) {
        return new VariableDeclaration(Identifier.of(id), init);
    }

    public static VariableDeclaration var(String id) {
        return new VariableDeclaration(Identifier.of(id));
    }

    public static VariableDeclaration of(Expression id, PathIdentifier type, Expression init) {
        return new VariableDeclaration(id, type, init);
    }

    public static VariableDeclaration of(Expression id, PathIdentifier type) {
        return new VariableDeclaration(id, type);
    }

    public static VariableDeclaration of(Expression id) {
        return new VariableDeclaration(id);
    }

    public static VariableDeclaration var(Expression id) {
        return new VariableDeclaration(id);
    }

    public static VariableDeclaration of(String id) {
        return new VariableDeclaration(Identifier.of(id));
    }

    public static VariableStatement var(Identifier id, PathIdentifier type, Expression init) {
        return (VariableStatement) VariableStatement.of(of(id, type, init));
    }

    public static VariableDeclaration var(String id, PathIdentifier type, Expression init) {
        return VariableDeclaration.of(Identifier.of(id), type, init);
    }

    public boolean hasInit() {
        return init != null;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public boolean hasType() {
        return type != null;
    }
}
