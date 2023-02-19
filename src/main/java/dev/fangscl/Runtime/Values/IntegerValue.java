package dev.fangscl.Runtime.Values;

import dev.fangscl.Runtime.TypeSystem.Literals.IntegerLiteral;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerValue extends RuntimeValue {
    private int value;

    public IntegerValue(int number) {
        this.value = number;
        this.type = ValueType.Integer;
    }

    public IntegerValue(IntegerLiteral number) {
        this(number.getValue());
    }
}
