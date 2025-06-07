package io.zmeu.api.resource;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Logical identity of a resource.
 * It can hold:
 * 1. Logical name of the resource (user-defined)
 * 2. namespace for scoping (e.g., dev, prod, team-a)
 * 3. region helps localize identity in cloud-native systems
 * 4. account id useful if you’re deploying to multiple AWS/GCP accounts
 * 5. kind represents resource type (VM, bucket)
 * 6. version helps detect updates or conflicts over time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Identity {
    private String name;
    @Transient
    private String renamedFrom;

    public Identity(String name) {
        this.name = name;
    }

}
