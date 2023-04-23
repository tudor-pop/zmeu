package dev.fangscl.Runtime.Values;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class NullValue extends RuntimeValue<Object> {
    private static final NullValue value = new NullValue();

    public NullValue() {
        this.type = ValueType.Null;
    }

    @Override
    @Nullable
    public Object getRuntimeValue() {
        return value;
    }

    public static NullValue of() {
        return value;
    }
}
