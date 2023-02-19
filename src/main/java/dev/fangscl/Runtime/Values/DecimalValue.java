package dev.fangscl.Runtime.Values;

import dev.fangscl.Runtime.TypeSystem.Literals.DecimalLiteral;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalValue extends RuntimeValue {
    private double value;

    public DecimalValue(double number) {
        this.value = number;
        this.type = ValueType.Decimal;
    }

    public DecimalValue(float number) {
        this.value = number;
    }

    public DecimalValue(DecimalLiteral decimalLiteral) {
        this(decimalLiteral.getValue());
    }
}
