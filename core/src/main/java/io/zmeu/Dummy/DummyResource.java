package io.zmeu.Dummy;

import io.zmeu.api.annotations.Property;
import io.zmeu.api.annotations.Schema;
import io.zmeu.api.schema.Type;
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
    @Property(type = Type.String)
    private String name;
    @Property(type = Type.String)
    private String content;
    @Property(type = Type.String)
    private String uid;
}
