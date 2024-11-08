package io.zmeu.Diff;

import org.javers.core.Changes;
import org.jetbrains.annotations.Nullable;

public record Plan(@Nullable Object sourceCode, @Nullable Changes diffResults) {
}
