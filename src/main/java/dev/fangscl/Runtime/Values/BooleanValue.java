package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanValue extends RuntimeValue {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
        this.type = ValueType.Boolean;
    }
}
