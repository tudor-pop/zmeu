package io.zmeu.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Environment.IEnvironment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class SchemaValue {
    public static final String INSTANCES = "instances";
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Environment environment;
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Environment instances;
    private final Identifier type;

    private SchemaValue(Identifier type, Environment environment) {
        this.type = type;
        this.instances = new Environment(environment);
        this.environment = environment;
        this.environment.init(INSTANCES, instances);
    }

    public static SchemaValue of(Identifier name, Environment environment) {
        return new SchemaValue(name, environment);
    }

    public static SchemaValue schema(Identifier name, Environment environment) {
        return new SchemaValue(name, environment);
    }

    public static SchemaValue of(String name, Environment environment) {
        return new SchemaValue(Identifier.id(name), environment);
    }

    public String typeString() {
        return type.string();
    }

    @NotNull
    public FunValue getMethod(String methodName) {
        return (FunValue) environment.lookup(methodName, "Method not found: " + methodName);
    }

    @Nullable
    public FunValue getMethodOrNull(String methodName) {
        return (FunValue) environment.get(methodName);
    }

    public Object assign(String varName, Object value) {
        return environment.assign(varName, value);
    }

    public Object lookup(@Nullable String varName) {
        return environment.lookup(varName);
    }

    public Object lookup(@Nullable Object varName) {
        return environment.lookup(varName);
    }

    public IEnvironment getParent() {
        return environment.getParent();
    }

    public @Nullable Object get(String key) {
        return environment.get(key);
    }

    public Object init(String name, Object value) {
        return environment.init(name,value);
    }

    public Object initInstance(String name, Object instance) {
        return this.instances.init(name, instance);
    }

    public ResourceValue getInstance(String name) {
        return (ResourceValue) instances.get(name);
    }
}
