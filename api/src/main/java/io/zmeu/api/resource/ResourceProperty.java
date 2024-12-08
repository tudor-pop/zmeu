package io.zmeu.api.resource;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResourceProperty {
    private String name;
    private String type;
    private Object value;
    private Object defaultVal;
    private String description;
    private String deprecationMessage;
    private boolean required;
    private boolean recreateOnChange;
    private boolean hidden;

    public Object getValue() {
        return value == null ? defaultVal : value;
    }
}
