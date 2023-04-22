package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalValue extends RuntimeValue {
    private double value;

    public DecimalValue(double number) {
        this.value = number;
        this.type = ValueType.Decimal;
    }

    public DecimalValue(float number) {
        this.value = number;
    }

    public DecimalValue(NumericLiteral decimalLiteral) {
        this(decimalLiteral.getValue().doubleValue());
    }

    public static RuntimeValue of(Statement statement) {
        if (statement instanceof NumericLiteral s)
            return new DecimalValue(s.getValue().doubleValue());
        throw new IllegalStateException();
    }

    public static RuntimeValue of(double value) {
        return new DecimalValue(value);
    }
}
