package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Environment;
import dev.fangscl.Runtime.IEnvironment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
public class ResourceValue implements RuntimeValue<Identifier>, IEnvironment {
    private Environment environment;
    private Identifier name;
    private List<RuntimeValue<Object>> args;

    private ResourceValue(Identifier name, List<RuntimeValue<Object>> args, Environment environment) {
        this.name = name;
        this.args = args;
        this.environment = environment;
    }

    private ResourceValue(Identifier e) {
        this(e, Collections.emptyList(), new Environment());
    }

    @Override
    public Identifier getRuntimeValue() {
        return name;
    }

    public static RuntimeValue<Identifier> of(Identifier name, List<RuntimeValue<Object>> params, Environment environment) {
        return new ResourceValue(name, params, environment);
    }

    public static ResourceValue of(String name, List<RuntimeValue<Object>> params, Environment environment) {
        return (ResourceValue) ResourceValue.of(Identifier.of(name), params, environment);
    }


    public static RuntimeValue<Identifier> of(List<RuntimeValue<Object>> params, Environment environment) {
        return new ResourceValue(null, params, environment);
    }

    public static RuntimeValue<Identifier> of(Identifier string) {
        return ResourceValue.of(string, Collections.emptyList(), new Environment());
    }

    public static RuntimeValue<Identifier> of(String string) {
        return ResourceValue.of(Identifier.of(string));
    }

    public static RuntimeValue<Identifier> of(String string, Environment environment) {
        return ResourceValue.of(Identifier.of(string), Collections.emptyList(), environment);
    }


    @Nullable
    public String name() {
        if (name == null) return null ;
        else return name.getSymbol();
    }

    @Override
    public RuntimeValue assign(String varName, RuntimeValue value) {
        return environment.assign(varName, value);
    }

    @Override
    public RuntimeValue lookup(@Nullable String varName) {
        return environment.lookup(varName);
    }

    @Override
    public RuntimeValue lookup(@Nullable RuntimeValue<String> varName) {
        return environment.lookup(varName);
    }

    @Override
    public @Nullable RuntimeValue get(String key) {
        return environment.get(key);
    }
}
