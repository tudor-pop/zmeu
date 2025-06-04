package io.zmeu.Diff;

import io.zmeu.Utils.Reflections;
import io.zmeu.api.resource.Resource;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class DiffUtils {
    static void validate(@Nullable Resource resource) {
        if (resource == null) {
            return;
        }
        if (StringUtils.isBlank(resource.getResourceNameString())) {
            throw new IllegalArgumentException(resource + " is missing resource name");
        }
    }

    static void updateImmutableProperties(Resource source, Resource target) {
        for (Field property : target.getProperties().getClass().getDeclaredFields()) {
            if (Reflections.isImmutable(property)) {
                try {
                    Field field = source.getProperties().getClass().getDeclaredField(property.getName());
                    field.setAccessible(true);
                    property.setAccessible(true);

                    var sourceValue = property.get(source.getProperties());
                    // detect replacement. If immutable fields differ (src/cloud) then mark src for replacement by setting a new ID
                    // which will be detected as an remove/add operation
                    var targetProperty = BeanUtilsBean2.getInstance().getProperty(target.getProperties(), property.getName());
                    if (targetProperty != null && !Objects.equals(sourceValue, targetProperty)) {
//                        target.setId(UUID.randomUUID());
                        target.setReplace(true);
                        source.setReplace(true);
                        target.addImmutable(property.getName());
                        source.setImmutable(target.getImmutable());
                    } else {
                        BeanUtilsBean2.getInstance().copyProperty(target.getProperties(), property.getName(), sourceValue);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
