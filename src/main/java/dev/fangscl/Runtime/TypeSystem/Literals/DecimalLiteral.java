package dev.fangscl.Runtime.TypeSystem.Literals;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalLiteral extends Expression {
    private double value;

    public DecimalLiteral() {
        this.kind = NodeType.DecimalLiteral;
    }

    public DecimalLiteral(double value) {
        this();
        this.value = value;
    }
    public DecimalLiteral(String value) {
        this();
        this.value = Double.parseDouble(value);
    }
}
