package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Environment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
public class FunValue implements RuntimeValue<Identifier> {
    private Environment environment;
    private Identifier name;
    private List<Expression> params;
    private Statement body;

    private FunValue(Identifier name, List<Expression> params, Statement body, Environment environment) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.environment = environment;
    }

    private FunValue(Identifier e) {
        this(e, Collections.emptyList(), BlockStatement.of(), new Environment());
    }

    @Override
    public Identifier getRuntimeValue() {
        return name;
    }

    public static RuntimeValue<Identifier> of(Identifier name, List<Expression> params, Statement body, Environment environment) {
        return new FunValue(name, params, body, environment);
    }

    public static FunValue of(String name, List<Expression> params, Statement body, Environment environment) {
        return (FunValue) FunValue.of(Identifier.of(name), params, body, environment);
    }
    public static RuntimeValue<Identifier> of(String name, List<Expression> params,  Environment environment) {
        return FunValue.of(Identifier.of(name), params, BlockStatement.of(), environment);
    }

    public static RuntimeValue<Identifier> of(List<Expression> params, Statement body, Environment environment) {
        return new FunValue(null, params, body, environment);
    }

    public static RuntimeValue<Identifier> of(Identifier string) {
        return FunValue.of(string, Collections.emptyList(), BlockStatement.of(), new Environment());
    }

    public static RuntimeValue<Identifier> of(String string) {
        return FunValue.of(Identifier.of(string));
    }

    public static RuntimeValue<Identifier> of(String string, Environment environment) {
        return FunValue.of(Identifier.of(string), Collections.emptyList(), BlockStatement.of(), environment);
    }

    public static RuntimeValue<Identifier> of(Expression string) {
        if (string instanceof Identifier s)
            return new FunValue(s);
        throw new RuntimeException("Invalid variable name: " + string.toSExpression());
    }

    @Nullable
    public String name() {
        if (name == null) return null ;
        else return name.getSymbol();
    }
}
