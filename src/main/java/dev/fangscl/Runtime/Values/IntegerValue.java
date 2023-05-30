package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import lombok.Data;

@Data
public class IntegerValue implements RuntimeValue<Integer> {
    private int value;

    public IntegerValue(int number) {
        this.value = number;
    }

    public IntegerValue(NumericLiteral number) {
        this(number.getValue().intValue());
    }

    public static RuntimeValue of(Expression value) {
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
