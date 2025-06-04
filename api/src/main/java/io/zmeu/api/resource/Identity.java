package io.zmeu.api.resource;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
