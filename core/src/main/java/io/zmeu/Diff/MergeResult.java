package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import org.javers.core.Changes;
import org.jetbrains.annotations.Nullable;

public record MergeResult(Changes changes, @Nullable Resource resource) {

}
