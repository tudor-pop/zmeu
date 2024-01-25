package dev.fangscl.Backend;

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
    private String name;

    @DiffIgnore
    private String canonicalType;

    public Resource() {
    }

    public Resource(String name) {
        this.name = name;
    }
}
