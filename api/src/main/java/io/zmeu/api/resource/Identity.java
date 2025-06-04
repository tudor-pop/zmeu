package io.zmeu.api.resource;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Identity {
    private String name;
    private String renamedFrom;

    public Identity(String name) {
        this.name = name;
    }

}
