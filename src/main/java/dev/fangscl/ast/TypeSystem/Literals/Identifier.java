package dev.fangscl.ast.TypeSystem.Literals;

import dev.fangscl.ast.TypeSystem.Base.Expression;
import dev.fangscl.ast.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Identifier extends Expression {
    private String symbol;

    public Identifier() {
        this.kind = NodeType.Identifier;
    }

    public Identifier(String symbol) {
        this();
        this.symbol = symbol;
    }
}
