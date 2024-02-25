package io.zmeu.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.Expressions.PackageIdentifier;
import io.zmeu.Runtime.Environment.Environment;
import io.zmeu.Runtime.Environment.IEnvironment;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class SchemaValue {
    public static final String INSTANCES = "instances";
    @JsonIgnore
    private final Environment environment;
    @JsonIgnore
    private final Environment instances;
    private final PackageIdentifier type;

    private SchemaValue(PackageIdentifier type, Environment environment) {
        this.type = type;
        this.instances = new Environment(environment);
        this.environment = environment;
        this.environment.init(INSTANCES, instances);
    }

    public static SchemaValue of(PackageIdentifier name, Environment environment) {
        return new SchemaValue(name, environment);
    }

    public static SchemaValue of(String name, Environment environment) {
        return new SchemaValue(PackageIdentifier.of(name), environment);
    }

    public String typeString() {
        return type.getSymbol();
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
}
