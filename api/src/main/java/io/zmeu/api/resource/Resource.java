package io.zmeu.api.resource;

import io.zmeu.api.annotations.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
public class Resource {
    // must be ignored during diffs because if we don't it will show up as a resource property
    // however we need to use this as an Id
    @Id
    @DiffIgnore
    private UUID id = UUID.randomUUID();
    @DiffIgnore
    private ResourceName resourceName;
    private Set<String> dependencies;
    private Object resource;
    private String type;
    private Boolean replace;
    @DiffIgnore
    private Set<String> immutable;
    /**
     * indicate if this resource should exist in cloud
     */
    private Boolean existing;

    public Resource() {
    }

    public Resource(String resourceName) {
        this.resourceName = new ResourceName(resourceName);
    }

    public Resource(String resourceName, Object resource) {
        this.resourceName = new ResourceName(resourceName);
        setResource(resource);
    }

    public Resource(ResourceName resourceName, Object resource) {
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

    public void setResourceName(String resourceName) {
        this.resourceName = new ResourceName(resourceName);
    }

    public String getResourceName() {
        return resourceName.getName();
    }

    public ResourceName resourceName() {
        return resourceName;
    }

    public void setReplace(Boolean replace) {
        this.replace = replace;
        if (this.immutable == null) {
            this.immutable = HashSet.newHashSet(1);
        }
    }

    public void addImmutable(String immutable) {
        if (this.immutable == null) {
            this.immutable = new HashSet<>();
        }
        this.immutable.add(immutable);
    }
}
