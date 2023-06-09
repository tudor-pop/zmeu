package dev.fangscl.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Environment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
public class FunValue {
    @JsonBackReference
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
        this(e, Collections.emptyList(), ExpressionStatement.of(BlockExpression.of()), new Environment());
    }

    public Identifier getRuntimeValue() {
        return name;
    }

    public static Object of(Identifier name, List<Expression> params, Statement body, Environment environment) {
        return new FunValue(name, params, body, environment);
    }

    public static FunValue of(String name, List<Expression> params, Statement body, Environment environment) {
        return (FunValue) FunValue.of(Identifier.of(name), params, body, environment);
    }
    public static Object of(String name, List<Expression> params,  Environment environment) {
        return FunValue.of(Identifier.of(name), params, ExpressionStatement.of(BlockExpression.of()), environment);
    }

    public static Object of(List<Expression> params, Statement body, Environment environment) {
        return new FunValue(null, params, body, environment);
    }

    public static Object of(Identifier string) {
        return FunValue.of(string, Collections.emptyList(), ExpressionStatement.of(BlockExpression.of()), new Environment());
    }

    public static Object of(String string) {
        return FunValue.of(Identifier.of(string));
    }

    public static Object of(String string, Environment environment) {
        return FunValue.of(Identifier.of(string), Collections.emptyList(), ExpressionStatement.of(BlockExpression.of()), environment);
    }

    public static Object of(Expression string) {
        if (string instanceof Identifier s)
            return new FunValue(s);
        throw new RuntimeException("Invalid variable name: " + string.toString());
    }

    @Nullable
    public String name() {
        if (name == null) return null ;
        else return name.getSymbol();
    }
}
