package dev.fangscl.Runtime.Values;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class NullValue implements RuntimeValue<Object> {
    private static final NullValue value = new NullValue();

    public NullValue() {
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
