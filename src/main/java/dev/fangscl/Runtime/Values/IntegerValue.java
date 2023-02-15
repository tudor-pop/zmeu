package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerValue extends RuntimeValue {
    private int value;

    public IntegerValue(int number) {
        this.value = number;
        this.type = ValueType.Number;
    }
}
