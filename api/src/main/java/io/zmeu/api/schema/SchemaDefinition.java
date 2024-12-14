package io.zmeu.api.schema;

import io.zmeu.api.resource.ResourceProperty;
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

    private Class resourceClass;

    private Map<String, ResourceProperty> properties;

    public static String toString(Object resource) {
        var fields = resource.getClass().getDeclaredFields();
        var properties = new StringBuilder(" { \n");
        for (Field field : fields) {
            var property = field.getAnnotation(Property.class);
            var name = property.name().isBlank() ? field.getName() : property.name();
            if (property.readonly()) {
                properties.append("\treadonly ");
            } else {
                properties.append("\tvar ");
            }
            properties.append(name);
            properties.append("\t");
            properties.append(property.type().name());
            properties.append("\n");
        }
        properties.append("} \n");

        var annotation = resource.getClass().getAnnotation(Schema.class);
        return "schema %s%s".formatted(annotation.typeName(), properties);
    }

    public static SchemaDefinition toSchema(Object resource) {
        var builder = SchemaDefinition.builder();
        var schemaDefinition = resource.getClass().getAnnotation(Schema.class);
        if (schemaDefinition == null) {
            throw new RuntimeException("@SchemaDefinition annotation not found on class: "+resource.getClass().getName());
        }

        builder.name(schemaDefinition.typeName());
        builder.properties(new HashMap<>());
        builder.resourceClass(resource.getClass());

        var fields = resource.getClass().getDeclaredFields();
        for (Field field : fields) {
            var property = field.getAnnotation(Property.class);
            var attribute = ResourceProperty.builder();

            attribute.required(property.optional());
            attribute.type(property.type().name());
            attribute.readOnly(property.readonly());

            String name = property.name().isBlank() ? field.getName() : property.name();
            attribute.name(name);
            attribute.recreateOnChange(property.recreateOnChange());

            builder.properties.put(name, attribute.build());
        }

        return builder.build();
    }
}
