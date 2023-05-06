package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.RuntimeValue;
import org.jetbrains.annotations.Nullable;

public interface IEnvironment {
    RuntimeValue assign(String varName, RuntimeValue value);

    RuntimeValue lookup(@Nullable String varName);

    RuntimeValue lookup(@Nullable RuntimeValue<String> varName);
}
