package io.zmeu.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class Schemas {

   public List<SchemaDefinition> items;

}
