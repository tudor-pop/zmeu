package dev.fangscl.Runtime.Values;

import lombok.Data;

@Data
public abstract class RuntimeValue<R> {
    protected ValueType type;

    public abstract R getRuntimeValue();
}
