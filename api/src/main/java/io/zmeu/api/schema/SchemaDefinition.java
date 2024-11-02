package io.zmeu.api.schema;

import io.zmeu.api.Attribute;
import io.zmeu.api.Property;
import io.zmeu.api.Resource;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class SchemaDefinition {
    private String name;

    private String description;

    private String uri;

    private String version;

    private Map<String, Attribute> properties;

    public static String toSchema(Resource resource) {
        var annotation = resource.getClass().getAnnotation(io.zmeu.api.SchemaDefinition.class);
        var fields = resource.getClass().getDeclaredFields();
        var properties = new StringBuilder();
        properties.append(" { \n");
        for (Field field : fields) {
            var property = field.getAnnotation(Property.class);
            var name = property.name().isBlank() ? field.getName() : property.name();
            if (property.readonly()) {
                properties.append("\tval ");
            } else {
                properties.append("\tvar ");
            }
            properties.append(name);
            properties.append("\t:");
            properties.append(property.type().name());
            properties.append("\n");
        }
        properties.append("} \n");

        return "schema " + annotation.typeName() + properties;
    }

    public static SchemaDefinition toSchemaDefinition(Resource resource) {
        var schemaDefinition = io.zmeu.api.schema.SchemaDefinition.builder();
        schemaDefinition.name(resource.getClass().getAnnotation(io.zmeu.api.SchemaDefinition.class).typeName());
        schemaDefinition.properties(new HashMap<>());

        var fields = resource.getClass().getDeclaredFields();
        for (Field field : fields) {
            var property = field.getAnnotation(Property.class);
            var attribute = Attribute.builder();
            attribute.required(property.optional());
            attribute.type(property.type().name());

            String name = property.name().isBlank() ? field.getName() : property.name();
            attribute.name(name);
            schemaDefinition.properties.put(name, attribute.build());
        }

        return schemaDefinition.build();
    }
}
