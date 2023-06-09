package dev.fangscl.Runtime;

import org.jetbrains.annotations.Nullable;

public interface IEnvironment {
    Object assign(String varName, Object value);

    Object lookup(@Nullable String varName);

    Object lookup(@Nullable Object varName);

    @Nullable Object get(String key);
}
