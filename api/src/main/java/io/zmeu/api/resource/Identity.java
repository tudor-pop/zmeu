package io.zmeu.api.resource;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Identity {
    private String name, renamedFrom;
    // must be ignored during diffs because if we don't it will show up as a resource property
    // however we need to use this as an Id to implement stable renaming of resources
    @DiffIgnore
    private String id = UUID.randomUUID().toString();

    public Identity(String name, String renamedFrom) {
        this.name = name;
        this.renamedFrom = renamedFrom;
    }

    public Identity(String name) {
        this.name = name;
    }
}
