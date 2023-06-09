package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import lombok.Data;

@Data
public class BooleanValue {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }
    public static BooleanValue of(boolean statement) {
        return new BooleanValue(statement);
    }

    public static BooleanValue of(Expression statement) {
        if (statement instanceof BooleanLiteral s)
            return new BooleanValue(s.isValue());
        throw new IllegalStateException();
    }

    public Boolean getRuntimeValue() {
        return value;
    }
}
