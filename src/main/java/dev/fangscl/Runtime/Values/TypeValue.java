package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Environment;
import dev.fangscl.Runtime.IEnvironment;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TypeValue implements IEnvironment {
    private Environment environment;
    private Identifier type;

    private TypeValue(Identifier type, Environment environment) {
        this.type = type;
        this.environment = environment;
    }

    public Identifier getRuntimeValue() {
        return type;
    }

    public static TypeValue of(Identifier name, Environment environment) {
        return new TypeValue(name, environment);
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

    @Override
    public Object assign(String varName, Object value) {
        return environment.assign(varName, value);
    }

    @Override
    public Object lookup(@Nullable String varName) {
        return environment.lookup(varName);
    }

    @Override
    public Object lookup(@Nullable Object varName) {
        return environment.lookup(varName);
    }

    @Override
    public @Nullable Object get(String key) {
        return environment.get(key);
    }

}
