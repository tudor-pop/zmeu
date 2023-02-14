package dev.fangscl.ast.TypeSystem.Literals;

import dev.fangscl.ast.TypeSystem.Base.Expression;
import dev.fangscl.ast.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerLiteral extends Expression {
    private int value;

    public IntegerLiteral() {
        this.kind = NodeType.NumericLiteral;
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
