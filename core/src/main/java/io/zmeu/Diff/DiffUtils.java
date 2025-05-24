package io.zmeu.Diff;

import io.zmeu.Utils.Reflections;
import io.zmeu.api.resource.Resource;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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
        for (Field property : target.getResource().getClass().getDeclaredFields()) {
            if (Reflections.isReadOnly(property)) {
                try {
                    Field field = source.getResource().getClass().getDeclaredField(property.getName());
                    field.setAccessible(true);
                    property.setAccessible(true);
                    BeanUtilsBean2.getInstance().copyProperty(target.getResource(), property.getName(), property.get(source.getResource()));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
