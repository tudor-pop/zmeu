package io.zmeu.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.Map;

@Data
@SuperBuilder
@Entity
@EqualsAndHashCode
public class Resource {
    // must be ignored during diffs because if we don't it will show up as a resource property
    // however we need to use this as an Id
    @Id
    @DiffIgnore
    private String resourceName;

    private Object resource;

    public Resource() {
    }

    public Resource(Object resource) {
        this.resource = resource;
    }

    public Resource(String resourceName,  Object resource) {
        this.resourceName = resourceName;
        this.resource = resource;
    }

}
