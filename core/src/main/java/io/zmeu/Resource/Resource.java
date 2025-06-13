package io.zmeu.Resource;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.zmeu.api.annotations.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "Resource")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Table(name = "resources")
public class Resource extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "identity_id")
    @EqualsAndHashCode.Include
    private Identity identity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "resource_type_id")
    @EqualsAndHashCode.Include
    private ResourceType type;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @EqualsAndHashCode.Include
    private Object properties;

    @Transient
    private Set<String> dependencies;

    /**
     * reason of replacement. Could be because an immutable property has changed or any other future cases
     */
    @DiffIgnore
    @Transient
    private ReplaceReason replace;
    /**
     * indicate if this resource should exist in cloud
     */
    private Boolean existing;

    public Resource() {
    }

    public Resource(String resourceName) {
        setIdentity(resourceName);
    }

    public Resource(String resourceName, Object properties) {
        setIdentity(resourceName);
        setProperties(properties);
    }

    public Resource(Identity identity, Object properties) {
        setIdentity(identity);
        setProperties(properties);
    }

    public void setIdentity(String resourceName) {
        this.identity = new Identity(resourceName);
        this.identity.setResource(this);
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
        this.identity.setResource(this);
    }

    public void setProperties(Object resource) {
        this.properties = resource;
        if (!(resource instanceof Map<?,?>)) {
            this.type = new ResourceType(resource.getClass().getAnnotation(Schema.class).typeName());
        }
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

    public Identity resourceName() {
        return identity;
    }

    public String getResourceNameString() {
        return identity.getName();
    }

    public boolean isReplace() {
        return replace != null && replace.hasImmutableProperties();
    }

    public boolean hasImmutablePropetyChanged(String name) {
        return isReplace() && replace.isImmutable(name);
    }

    public void setResourceName(String resourceName) {
        if (identity == null) {
            this.identity = new Identity(resourceName);
        } else {
            identity.setName(resourceName);
        }
    }

    public boolean hasIdentity() {
        return identity != null;
    }

    public String getKind() {
        return type.getKind();
    }


    public Boolean isExisting() {
        return existing != null && existing;
    }
}
