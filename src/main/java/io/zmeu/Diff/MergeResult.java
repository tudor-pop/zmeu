package io.zmeu.Diff;

import io.zmeu.api.Resource;
import org.javers.core.Changes;
import org.jetbrains.annotations.Nullable;

public record MergeResult(Changes changes, @Nullable Resource resource) {

}
