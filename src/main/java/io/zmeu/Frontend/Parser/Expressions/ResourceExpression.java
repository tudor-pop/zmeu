package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.Statement;
import io.zmeu.Frontend.Parser.Statements.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExpression extends Statement {
    private Identifier type;
    @Nullable
    private Identifier name;
    private BlockExpression block;

    private ResourceExpression() {
        this.kind = NodeType.ResourceExpression;
    }

    private ResourceExpression(Identifier type, Identifier name, BlockExpression block) {
        this();
        this.type = type;
        this.name = name;
        this.block = block;
    }

    public static Statement of(Identifier type, Identifier name, BlockExpression block) {
        return new ResourceExpression(type, name, block);
    }

    public static Statement of() {
        return new ResourceExpression();
    }

    public List<Statement> getArguments() {
        return block.getExpression();
    }

    public String name() {
        return name.getSymbol();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
