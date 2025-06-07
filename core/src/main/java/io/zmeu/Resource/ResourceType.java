package io.zmeu.Resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffIgnoreProperties;
import org.javers.core.metamodel.annotation.ValueObject;

@Entity
@Data
@DiffIgnoreProperties("id")
@ValueObject
public class ResourceType {
    @Id
    @GeneratedValue
    @DiffIgnore
    private Integer id;
    @Column(nullable = false)
    private String kind;

    public ResourceType() {
    }

    public ResourceType(Integer id) {
        this.id = id;
    }

    public ResourceType(String kind) {
        this.kind = kind;
    }
}
