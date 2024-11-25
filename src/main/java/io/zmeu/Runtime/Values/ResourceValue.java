package io.zmeu.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Runtime.Environment.Environment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class ResourceValue  {
    private Environment properties;
    private SchemaValue schema;
    @JsonProperty("resourceName")
    private String name;
    private Set<String> dependencies;

    public ResourceValue() {
    }

    public ResourceValue(String name, Environment parent) {
        this.name = name;
        this.properties = parent;
    }

    public ResourceValue(String name, Environment parent, @NonNull SchemaValue schema) {
        this.name = name;
        this.properties = parent;
        this.schema = schema;
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

    public void addDependency(String dependency) {
        getDependencies().add(dependency);
    }

    public Set<String> getDependencies() {
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public boolean hasDependencies() {
        return !getDependencies().isEmpty();
    }

    public record Data(String name, Map<String, Object> args) {
    }

}
