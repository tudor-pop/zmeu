package io.zmeu.api.schema;

import io.zmeu.api.Property;
import io.zmeu.api.Resource;
import io.zmeu.api.Schema;

import java.lang.reflect.Field;

public class ResourceToSchemaConverter {
    public static String toSchema(Resource resource) {
        var annotation = resource.getClass().getAnnotation(Schema.class);
        var fields = resource.getClass().getDeclaredFields();
        StringBuilder properties = new StringBuilder();
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
            properties.append("\t:\t");
            properties.append(property.type());
            properties.append("\n");
        }
        properties.append("} \n");

        return "schema " + annotation.typeName() + properties;
    }
}
