package io.zmeu.Diff;

import io.zmeu.Utils.Reflections;
import io.zmeu.api.resource.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class DiffUtils {
    static void validate(@Nullable Resource resource) {
        if (resource == null) {
            return;
        }
        if (StringUtils.isBlank(resource.getResourceName())) {
            throw new IllegalArgumentException(resource + " is missing resource name");
        }
    }

    static void updateReadOnlyProperties(Resource source, Resource target) {
        for (Field property : target.getClass().getDeclaredFields()) {
            if (Reflections.isReadOnly(property)) {
                try {
                    Field field = source.getClass().getDeclaredField(property.getName());
                    field.setAccessible(true);
                    property.setAccessible(true);
                    property.set(target, field.get(source));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
