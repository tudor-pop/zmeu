package io.zmeu.api.resource;

import io.zmeu.api.schema.Type;
import lombok.Builder;

@Builder
public record Property(
        String name,
        Type type,
        Object value,
        Object defaultVal,
        String description,
        String deprecationMessage,
        boolean required,
        boolean readOnly,
        boolean recreateOnChange,
        boolean hidden) {

    public Object getValue() {
        return value == null ? defaultVal : value;
    }
}
