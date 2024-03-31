package io.zmeu.Runtime.Values;

import io.zmeu.Frontend.Parser.Literals.PathIdentifier;
import io.zmeu.Runtime.Environment.Environment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ResourceValue {
    private Environment properties;
    private SchemaValue schema;
    private String name;

    public ResourceValue() {
    }

    private ResourceValue(String name, Environment parent) {
        this.name = name;
        this.properties = parent;
    }

    private ResourceValue(String name, Environment parent, @NonNull SchemaValue schema) {
        this.name = name;
        this.properties = parent;
        this.schema = schema;
    }

    private ResourceValue(String e) {
        this(e, new Environment());
    }

    public static Object of(String string) {
        return ResourceValue.of(string, new Environment());
    }

    public static ResourceValue of(String string, Environment environment) {
        return new ResourceValue(string, environment);
    }
    public static ResourceValue of(String string, Environment environment, SchemaValue schema) {
        return new ResourceValue(string, environment, schema);
    }

    public Object argVal(String name) {
        return properties.get(name);
    }

    @Nullable
    public String name() {
        return name;
    }

    public Object assign(String varName, Object value) {
        return properties.assign(varName, value);
    }

    public Object lookup(@Nullable String varName) {
        return properties.lookup(varName);
    }

    public Object lookup(@Nullable Object varName) {
        return properties.lookup(varName);
    }

    public @Nullable Object get(String key) {
        return properties.get(key);
    }

    public Object init(String name, Object value) {
        return properties.init(name, value);
    }

    public record Data(String name, Map<String, Object> args) {
    }

    public Data asData() {
        var entries = properties.getVariables().entrySet();
        var args = new HashMap<String, Object>(entries.size());
        for (var it : entries) {
            args.put(it.getKey(), it.getValue());
        }
        return new Data(this.name(), args);
    }


    public PathIdentifier type() {
        return (PathIdentifier) this.schema.getType();
    }
    public String typeString() {
        return this.schema.typeString();
    }

}
