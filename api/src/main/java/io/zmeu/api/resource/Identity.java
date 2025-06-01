package io.zmeu.api.resource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Identity {
    private String name, renamedFrom;
    /*
    * Used to implement stable resource renaming. It should be one of:
    * 1. ARN
    * 2. cloud unique name
    * 3. a composite key between resource name and some other property
    * 4. UUID
    * */
    private String id;

    public Identity(String name, String renamedFrom) {
        this.name = name;
        this.renamedFrom = renamedFrom;
    }

    public Identity(String name) {
        this.name = name;
    }
}
