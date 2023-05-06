package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Runtime.Environment;
import dev.fangscl.Runtime.IEnvironment;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class SchemaValue implements RuntimeValue<Identifier>, IEnvironment {
    private Environment environment;
    private Identifier name;
    private BlockStatement body;

    private SchemaValue(Identifier name, BlockStatement body, Environment environment) {
        this.name = name;
        this.body = body;
        this.environment = environment;
    }

    @Override
    public Identifier getRuntimeValue() {
        return name;
    }

    public static SchemaValue of(Identifier name, BlockStatement body, Environment environment) {
        return new SchemaValue(name, body, environment);
    }

    public String getNameString() {
        return name.getSymbol();
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
