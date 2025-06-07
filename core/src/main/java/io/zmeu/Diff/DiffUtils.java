package io.zmeu.Diff;

import io.zmeu.Resource.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class DiffUtils {
    static void validate(@Nullable Resource resource) {
        if (resource == null) {
            return;
        }
        if (StringUtils.isBlank(resource.getResourceNameString())) {
            throw new IllegalArgumentException(resource + " is missing resource name");
        }
    }

}
