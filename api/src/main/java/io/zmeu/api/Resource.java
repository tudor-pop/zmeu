package io.zmeu.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

@Data
@SuperBuilder
@Entity
@EqualsAndHashCode
public class Resource {
    @Id
    @DiffIgnore
    private String resourceName;

    public Resource() {
    }

}
