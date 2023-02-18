package dev.fangscl.Runtime.TypeSystem.Literals;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerLiteral extends Expression {
    private int value;

    public IntegerLiteral() {
        this.kind = NodeType.IntegerLiteral;
    }

    public IntegerLiteral(int value) {
        this();
        this.value = value;
    }

    public IntegerLiteral(String value) {
        this();
        this.value = Integer.parseInt(value);
    }
}
