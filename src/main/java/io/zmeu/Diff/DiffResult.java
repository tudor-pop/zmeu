package io.zmeu.Diff;

import io.zmeu.api.Resource;
import lombok.Data;
import org.javers.core.Changes;
import org.jetbrains.annotations.Nullable;

@Data
public class DiffResult {
    private Changes changes;
    @Nullable
    private Resource resource;

    public DiffResult(Changes changes, @Nullable Resource resource) {
        this.changes = changes;
        this.resource = resource;
    }
}