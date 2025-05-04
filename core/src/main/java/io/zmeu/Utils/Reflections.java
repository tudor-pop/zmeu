package io.zmeu.Utils;

import io.zmeu.api.annotations.Property;

import java.lang.reflect.Field;

public class Reflections {
    public static boolean isReadOnly(Field field) {
        Property annotation = field.getAnnotation(Property.class);
        return annotation != null && annotation.readonly();
    }
}
