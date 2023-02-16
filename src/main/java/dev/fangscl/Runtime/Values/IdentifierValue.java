package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentifierValue extends RuntimeValue {
    private RuntimeValue value;

    public IdentifierValue(RuntimeValue number) {
        this.value = number;
        this.type = ValueType.Identifier;
    }
}
