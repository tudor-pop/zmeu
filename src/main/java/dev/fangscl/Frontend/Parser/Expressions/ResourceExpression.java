package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.Statement;
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
    public <R> R accept(dev.fangscl.Frontend.Parser.Statements.Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
