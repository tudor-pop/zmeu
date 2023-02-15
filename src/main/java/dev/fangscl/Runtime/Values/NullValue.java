package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NullValue extends RuntimeValue {
    private String value = "null";

    public NullValue() {
        this.type = ValueType.Null;
    }
}
