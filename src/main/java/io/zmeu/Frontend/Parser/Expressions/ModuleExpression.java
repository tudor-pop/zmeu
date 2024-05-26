package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.Statement;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModuleExpression extends Statement {
    private Identifier type;
    @Nullable
    private Identifier name;
    private BlockExpression block;

    private ModuleExpression() {
        this.kind = NodeType.ModuleExpression;
    }

    private ModuleExpression(Identifier type, Identifier name, BlockExpression block) {
        this();
        this.type = type;
        this.name = name;
        this.block = block;
    }

    public static Statement of(Identifier type, Identifier name, BlockExpression block) {
        return new ModuleExpression(type, name, block);
    }

    public static Statement of() {
        return new ModuleExpression();
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