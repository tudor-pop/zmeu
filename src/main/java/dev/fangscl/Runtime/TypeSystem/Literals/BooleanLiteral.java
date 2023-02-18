package dev.fangscl.Runtime.TypeSystem.Literals;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.NodeType;
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
