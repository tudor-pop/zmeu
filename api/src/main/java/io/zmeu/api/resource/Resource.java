package io.zmeu.api.resource;

import io.zmeu.api.annotations.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.HashSet;
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
    private Object properties;
    private String type;
    /**
     * indicate if this resource should only read from cloud and not write/update the cloud/javers state
     */
    private Boolean readOnly;

    public Resource() {
    }

    public Resource(Object properties) {
        setProperties(properties);
    }

    public Resource(String resourceName) {
        this.resourceName = resourceName;
    }
    public Resource(String resourceName, Object properties) {
        this.resourceName = resourceName;
        setProperties(properties);
    }

    public void setProperties(Object properties) {
        this.properties = properties;
        this.type = properties.getClass().getAnnotation(Schema.class).typeName();
    }

    public Set<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public Class<?> getResourceClass() {
        return properties.getClass();
    }
}
