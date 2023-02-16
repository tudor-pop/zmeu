package dev.fangscl.ast.TypeSystem.Literals;

import dev.fangscl.ast.TypeSystem.Base.Expression;
import dev.fangscl.ast.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanLiteral extends Expression {
    private boolean value;

    public BooleanLiteral() {
        this.kind = NodeType.BooleanLiteral;
    }

    public BooleanLiteral(boolean value) {
        this();
        this.value = value;
    }

    public BooleanLiteral(String value) {
        this();
        this.value = Boolean.parseBoolean(value);
    }
}
