package io.zmeu.Diff;

import org.javers.core.ChangesByObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Plan(@Nullable Object sourceCode, List<ChangesByObject> diffResults) {
}
