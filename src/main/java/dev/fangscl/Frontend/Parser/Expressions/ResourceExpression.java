package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * AssignmentExpression
 * : AdditiveExpression
 * | LeftHandSideExpression AssignmentOperator AssignmentExpression
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExpression extends Expression {
    private Identifier type;
    @Nullable
    private Identifier name;
    private BlockStatement block;

    private ResourceExpression() {
        this.kind = NodeType.ResourceExpression;
    }

    private ResourceExpression(Identifier type, Identifier name, BlockStatement block) {
        this();
        this.type = type;
        this.name = name;
        this.block = block;
    }

    public static Expression of(Identifier type, Identifier name, BlockStatement block) {
        return new ResourceExpression(type, name, block);
    }

    public static Expression of() {
        return new ResourceExpression();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSExpression() {
        return "(" + type + ")";
    }

    public List<Statement> getArguments() {
        return block.getExpression();
    }

    public String name() {
        return name.getSymbol();
    }
}
