package dev.fangscl.Backend;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

@Data
@SuperBuilder
@Entity
public class Resource {
    @Id
    private String id;
    private String name;
    protected String type;

    public Resource() {
        type = getClass().getName();
    }

    public Resource(String name, String type) {
        this(name);
    }

    public Resource(String name) {
        this();
        this.name = name;
    }
}
