package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Environment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FunValue extends RuntimeValue<Identifier> {
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
        this(e, Collections.emptyList(), BlockExpression.of(), new Environment());
    }

    @Override
    public Identifier getRuntimeValue() {
        return name;
    }

    public static RuntimeValue<Identifier> of(Identifier name, List<Expression> params, Statement body, Environment environment) {
        return new FunValue(name, params, body, environment);
    }
    public static RuntimeValue<Identifier> of(Identifier name, Statement body) {
        return new FunValue(name, Collections.emptyList(), body, new Environment());
    }

    public static RuntimeValue<Identifier> of(Identifier string) {
        return new FunValue(string);
    }

    public static RuntimeValue<Identifier> of(Expression string) {
        if (string instanceof Identifier s)
            return new FunValue(s);
        throw new RuntimeException("Invalid variable name: " + string.toSExpression());
    }
}