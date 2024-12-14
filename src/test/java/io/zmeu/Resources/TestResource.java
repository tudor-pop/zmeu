package io.zmeu.Resources;

import io.zmeu.api.resource.Resource;
import io.zmeu.api.schema.Property;
import io.zmeu.api.schema.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "", typeName = "TestResource")
public class TestResource extends Resource {
    private String name;
    private String content;

    @Property(type = Property.Type.String, readonly = true)
    private String uid;
}