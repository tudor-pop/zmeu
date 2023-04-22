package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerValue extends RuntimeValue<Integer> {
    private int value;

    public IntegerValue(int number) {
        this.value = number;
        this.type = ValueType.Integer;
    }

    public IntegerValue(NumericLiteral number) {
        this(number.getValue().intValue());
    }

    public static RuntimeValue of(Statement value) {
        if (value instanceof NumericLiteral s) {
            return new IntegerValue(s.getValue().intValue());
        }
        throw new IllegalStateException();
    }

    public static RuntimeValue of(int value) {
        return new IntegerValue(value);
    }

    @Override
    public Integer getRuntimeValue() {
        return value;
    }
}
