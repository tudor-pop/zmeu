package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
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

    public IntegerValue(NumericLiteral number) {
        this(number.getValue().intValue());
    }
}
