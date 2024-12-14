package io.zmeu.Diff;

import io.zmeu.Utils.Reflections;
import io.zmeu.api.resource.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class DiffUtils {
    static void validate(@Nullable Resource localState, Resource sourceState, @Nullable Resource cloudState) {
        if (localState != null && StringUtils.isBlank(localState.getResourceName())) {
            throw new IllegalArgumentException(localState + " is missing resource name");
        }
        if (sourceState != null && StringUtils.isBlank(sourceState.getResourceName())) {
            throw new IllegalArgumentException(sourceState + " is missing resource name");
        }
        if (cloudState != null && StringUtils.isBlank(cloudState.getResourceName())) {
            throw new IllegalArgumentException(cloudState + " is missing resource name");
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
