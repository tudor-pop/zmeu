package io.zmeu.api;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;

@Data
@SuperBuilder
@Entity
public class Resource<T> {
    @Id
    private String resourceName;

    private T instance;

    public Resource() {
    }

}
