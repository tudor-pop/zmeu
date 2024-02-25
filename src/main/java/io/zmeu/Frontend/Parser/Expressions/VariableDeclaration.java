package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VariableDeclaration extends Expression {
    private Identifier id;
    private Expression init;
    private TypeIdentifier type;

    public VariableDeclaration() {
        this.kind = NodeType.VariableDeclaration;
    }

    private VariableDeclaration(Expression id, Expression init) {
        this();
        this.id = (Identifier) id;
        this.init = init;
    }

    private VariableDeclaration(Expression id, TypeIdentifier type, Expression init) {
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

    public static VariableDeclaration of(Expression id, TypeIdentifier type, Expression init) {
        return new VariableDeclaration(id, type, init);
    }

    public static VariableDeclaration of(Expression id) {
        return new VariableDeclaration(id);
    }

    public static VariableDeclaration of(String id) {
        return new VariableDeclaration(Identifier.of(id));
    }

    public boolean hasInit() {
        return init != null;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
