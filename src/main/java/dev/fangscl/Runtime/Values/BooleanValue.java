package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;

@Data
public class BooleanValue implements RuntimeValue<Boolean> {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }
    public static RuntimeValue of(boolean statement) {
        return new BooleanValue(statement);
    }

    public static RuntimeValue of(Statement statement) {
        if (statement instanceof BooleanLiteral s)
            return new BooleanValue(s.isValue());
        throw new IllegalStateException();
    }

    @Override
    public Boolean getRuntimeValue() {
        return value;
    }
}
