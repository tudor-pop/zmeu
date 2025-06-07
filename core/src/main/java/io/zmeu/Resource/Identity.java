package io.zmeu.Resource;

import jakarta.persistence.*;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.ValueObject;

/**
 * Logical identity of a resource.
 * It can hold:
 * 1. Logical name of the resource (user-defined)
 * 2. namespace for scoping (e.g., dev, prod, team-a)
 * 3. region helps localize identity in cloud-native systems
 * 4. account id useful if you’re deploying to multiple AWS/GCP accounts
 * 6. version helps detect updates or conflicts over time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ValueObject
@Table(name = "identity", indexes = {
        @Index(name = "idx_name", columnList = "name")
})
@Entity(name = "Identity")
public class Identity {
    @Id
    @Column(name = "identity_id")
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @DiffIgnore
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "identity")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Resource resource;

    @Transient
    private String renamedFrom;

    public Identity(String name) {
        this.name = name;
    }

}
