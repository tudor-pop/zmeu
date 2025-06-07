package io.zmeu.Resource;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.ValueObject;

@Data
@ValueObject
@Entity(name = "ResourceType")
@Table(name = "resource_type")
public class ResourceType {

    @Id
    @DiffIgnore
    @Column(name = "resource_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String kind;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "type")
    @DiffIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Resource resource;

    public ResourceType() {
    }

    public ResourceType(Integer id) {
        this.id = id;
    }

    public ResourceType(String kind) {
        this.kind = kind;
    }
}
