package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Frontend.Parser.NodeType;
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
    public Identifier(boolean symbol) {
        this();
        this.symbol = String.valueOf(symbol);
    }
}
