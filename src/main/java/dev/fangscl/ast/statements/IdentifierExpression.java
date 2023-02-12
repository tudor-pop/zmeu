package dev.fangscl.ast.statements;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentifierExpression extends Expression {
    private String symbol;

    public IdentifierExpression() {
        this.kind = NodeType.Identifier;
    }
    public IdentifierExpression(String symbol) {
        this();
        this.symbol = symbol;
    }
}
