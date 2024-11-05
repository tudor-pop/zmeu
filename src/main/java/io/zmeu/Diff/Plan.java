package io.zmeu.Diff;

import org.jetbrains.annotations.Nullable;

public record Plan(@Nullable Object sourceCode,@Nullable Object diffResults) {
}
