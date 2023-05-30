package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import lombok.Data;

@Data
public class DecimalValue implements RuntimeValue<Double> {
    private double value;

    public DecimalValue(double number) {
        this.value = number;
    }

    public DecimalValue(float number) {
        this.value = number;
    }

    public DecimalValue(NumericLiteral decimalLiteral) {
        this(decimalLiteral.getValue().doubleValue());
    }

    public static RuntimeValue of(Expression statement) {
        if (statement instanceof NumericLiteral s)
            return new DecimalValue(s.getValue().doubleValue());
        throw new IllegalStateException();
    }

    public static RuntimeValue of(double value) {
        return new DecimalValue(value);
    }

    @Override
    public Double getRuntimeValue() {
        return value;
    }
}
