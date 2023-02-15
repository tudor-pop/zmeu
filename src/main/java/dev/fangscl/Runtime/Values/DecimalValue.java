package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalValue extends RuntimeValue {
    private double value;

    public DecimalValue(double number) {
        this.value = number;
        this.type = ValueType.Number;
    }
}
