package io.zmeu.api;

import io.zmeu.api.schema.SchemaDefinition;
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
