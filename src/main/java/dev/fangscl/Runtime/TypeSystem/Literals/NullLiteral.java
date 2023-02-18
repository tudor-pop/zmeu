package dev.fangscl.Runtime.TypeSystem.Literals;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NullLiteral extends Expression {
    private String value;

    public NullLiteral() {
        this.kind = NodeType.NullLiteral;
        this.value = "null";
    }

}
