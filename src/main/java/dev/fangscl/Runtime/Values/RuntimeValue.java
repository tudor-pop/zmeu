package dev.fangscl.Runtime.Values;

import lombok.Data;

@Data
public abstract class RuntimeValue<R> {
    public abstract R getRuntimeValue();
}
