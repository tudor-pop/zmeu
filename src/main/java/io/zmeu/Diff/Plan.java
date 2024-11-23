package io.zmeu.Diff;

import org.jetbrains.annotations.Nullable;

public record Plan(@Nullable Object sourceCode, java.util.List<org.javers.core.ChangesByObject> diffResults) {
}
