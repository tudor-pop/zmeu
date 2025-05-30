package io.zmeu.api.resource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourceName {
    private String name, renamedFrom;

    public ResourceName(String name, String renamedFrom) {
        this.name = name;
        this.renamedFrom = renamedFrom;
    }

    public ResourceName(String name) {
        this.name = name;
    }
}
