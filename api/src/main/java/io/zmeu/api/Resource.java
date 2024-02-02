package io.zmeu.api;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

@Data
@SuperBuilder
@Entity
public class Resource {
    @Id
    private String id;

    @DiffIgnore
    private String canonicalType;

    public Resource() {
    }

}
