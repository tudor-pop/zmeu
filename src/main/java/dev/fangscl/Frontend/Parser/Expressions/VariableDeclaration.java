package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VariableDeclaration extends Expression {
    private Identifier id;
    private Expression init;

    public VariableDeclaration() {
        this.kind = NodeType.VariableDeclaration;
    }

    private VariableDeclaration(Expression id, Expression init) {
        this();
        this.id = (Identifier) id;
        this.init = init;
    }
    private VariableDeclaration(Expression id) {
        this(id, null);
    }

    public static VariableDeclaration of(Expression id, Expression init) {
        return new VariableDeclaration(id, init);
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
    public String toSExpression() {
        return "(" + id.toSExpression() + " " + init.toSExpression() + ")";
    }
}
