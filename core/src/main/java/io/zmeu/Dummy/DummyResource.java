package io.zmeu.Dummy;

import io.zmeu.api.resource.Resource;
import io.zmeu.api.schema.Property;
import io.zmeu.api.schema.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.TypeName;

@SuperBuilder
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "", typeName = "DummyResource")
@TypeName("DummyResource")
public class DummyResource {
    @Property(type = Property.Type.String)
    private String name;
    @Property(type = Property.Type.String)
    private String content;
    @Property(type = Property.Type.String)
    private String uid;
}
