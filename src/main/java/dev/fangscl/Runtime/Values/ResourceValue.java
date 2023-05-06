package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Environment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
public class ResourceValue implements RuntimeValue<Identifier> {
    private Environment environment;
    private Identifier name;
    private List<Statement> args;

    private ResourceValue(Identifier name, List<Statement> args, Environment environment) {
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

    public static RuntimeValue<Identifier> of(Identifier name, List<Statement> params, Environment environment) {
        return new ResourceValue(name, params, environment);
    }

    public static ResourceValue of(String name, List<Statement> params, Environment environment) {
        return (ResourceValue) ResourceValue.of(Identifier.of(name), params, environment);
    }


    public static RuntimeValue<Identifier> of(List<Statement> params, Environment environment) {
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
}
