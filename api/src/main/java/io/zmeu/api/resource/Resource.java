package io.zmeu.api.resource;

import io.zmeu.api.schema.Property;
import io.zmeu.api.schema.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode
public class Resource {
    // must be ignored during diffs because if we don't it will show up as a resource property
    // however we need to use this as an Id
    @Id
    @DiffIgnore
    private String resourceName;

    private Set<String> dependencies;
    private Set<String> readOnly;
    private Object resource;
    private String type;

    public Resource() {
    }

    public Resource(Object resource) {
        setResource(resource);
    }

    public Resource(String resourceName) {
        this.resourceName = resourceName;
    }
    public Resource(String resourceName, Object resource) {
        this.resourceName = resourceName;
        setResource(resource);
    }

    public void setResource(Object resource) {
        this.resource = resource;
        this.type = resource.getClass().getAnnotation(Schema.class).typeName();
    }

    public Set<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public Class<?> getResourceClass() {
        return resource.getClass();
    }
}
