package io.zmeu.api.schema;

import io.zmeu.api.resource.Property;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Class represeting a schema {...} definition
 */
@Data
@Builder
public class Schema {
    private String name;

    private String description;

    private String uri;

    private String version;

    private Class resourceClass;

    private Set<Property> properties;

    public static String toString(Object resource) {
        var fields = resource.getClass().getDeclaredFields();
        var properties = new StringBuilder(" { \n");
        for (Field field : fields) {
            var property = field.getAnnotation(io.zmeu.api.annotations.Property.class);
            var name = property.name().isBlank() ? field.getName() : property.name();
            if (property.readonly()) {
                properties.append("\treadonly ");
            } else {
                properties.append("\tvar ");
            }
            properties.append(name);
            properties.append("\t:");
            properties.append(property.type().name());
            properties.append("\n");
        }
        properties.append("} \n");

        var annotation = resource.getClass().getAnnotation(io.zmeu.api.annotations.Schema.class);
        return "schema %s%s".formatted(annotation.typeName(), properties);
    }

    @SneakyThrows
    public static Schema toSchema(Object resource) {
        Objects.requireNonNull(resource);
        var builder = Schema.builder();
        var schemaDefinition = resource.getClass().getAnnotation(io.zmeu.api.annotations.Schema.class);
        if (schemaDefinition == null) {
            throw new RuntimeException("@SchemaDefinition annotation not found on class: "+resource.getClass().getName());
        }

        builder.name(schemaDefinition.typeName());
        builder.properties(new LinkedHashSet<>());
        builder.resourceClass(resource.getClass());

        var fields = resource.getClass().getDeclaredFields();
        for (Field field : fields) {
            var propertySchema = field.getAnnotation(io.zmeu.api.annotations.Property.class);
            var property = Property.builder();

            property.required(propertySchema.optional());
            property.type(propertySchema.type());


            property.readOnly(propertySchema.readonly());
            property.description(propertySchema.description());

            String name = propertySchema.name().isBlank() ? field.getName() : propertySchema.name();
            property.name(name);
            property.recreateOnChange(propertySchema.recreateOnChange());

            property.hidden(propertySchema.hidden());

            builder.properties.add(property.build());
        }

        return builder.build();
    }
}
