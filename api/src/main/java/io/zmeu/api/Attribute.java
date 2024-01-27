package io.zmeu.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Attribute {
    private String name;
    private String type;
    private String description;
    private String deprecationMessage;
    private boolean required;
    private boolean optional;
    private boolean hidden;
}
