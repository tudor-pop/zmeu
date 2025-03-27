package io.zmeu.api.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

import java.util.Set;

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

    private Set<String> dependencies;
    private Set<String> readOnly;

    public Resource() {
    }

    public Resource(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean hasResourceName() {
        return StringUtils.isBlank(resourceName);
    }

}
