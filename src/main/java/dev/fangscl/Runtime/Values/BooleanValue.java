package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanValue extends RuntimeValue<Boolean> {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
        this.type = ValueType.Boolean;
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
