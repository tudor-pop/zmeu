package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import org.javers.core.Changes;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.jetbrains.annotations.Nullable;

public record MergeResult(Changes changes, @Nullable Resource resource) {

    public boolean isNewResource() {
        for (Change change : changes) {
            if (change instanceof NewObject) {
                return true;
            }
        }
        return false;
    }

    public boolean isAddedResource() {
        return isNewResource();
    }

}
