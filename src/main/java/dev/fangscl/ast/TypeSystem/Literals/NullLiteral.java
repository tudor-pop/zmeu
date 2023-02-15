package dev.fangscl.ast.TypeSystem.Literals;

import dev.fangscl.ast.TypeSystem.Base.Expression;
import dev.fangscl.ast.TypeSystem.NodeType;
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
