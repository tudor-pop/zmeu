package io.zmeu.api.resource;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.zmeu.api.annotations.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded
    private Identity identity;
    private Set<String> dependencies;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Object resource;

    private String type;
    @DiffIgnore
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
        this.identity = new Identity(resourceName);
    }

    public Resource(String resourceName, Object resource) {
        this.identity = new Identity(resourceName);
        setResource(resource);
    }

    public Resource(Identity identity, Object resource) {
        this.identity = identity;
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

    public Identity resourceName() {
        return identity;
    }

    public String getResourceNameString() {
        return identity.getName();
    }


    public void addImmutable(String immutable) {
        if (this.immutable == null) {
            this.immutable = new HashSet<>();
        }
        this.immutable.add(immutable);
    }

    public boolean isReplace() {
        return replace != null && replace;
    }

    public void setResourceName(String resourceName) {
        identity.setName(resourceName);
    }
}
