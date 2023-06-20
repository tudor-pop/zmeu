package dev.fangscl.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Environment.Environment;
import dev.fangscl.Runtime.Interpreter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Data
public class FunValue implements Callable {
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private Environment clojure;
    private Identifier name;
    private List<Identifier> params;
    @EqualsAndHashCode.Exclude
    private Statement body;

    private FunValue(Identifier name, List<Identifier> params, Statement body, Environment clojure) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.clojure = clojure;
    }

    private FunValue(Identifier e) {
        this(e, Collections.emptyList(), ExpressionStatement.of(BlockExpression.of()), new Environment());
    }

    public Identifier getRuntimeValue() {
        return name;
    }

    public static Object of(Identifier name, List<Identifier> params, Statement body, Environment environment) {
        return new FunValue(name, params, body, environment);
    }

    public static FunValue of(String name, List<Identifier> params, Statement body, Environment environment) {
        return (FunValue) of(Identifier.of(name), params, body, environment);
    }
    public static Object of(String name, List<Identifier> params,  Environment environment) {
        return FunValue.of(Identifier.of(name), params, ExpressionStatement.of(BlockExpression.of()), environment);
    }

    public static Object of(List<Identifier> params, Statement body, Environment environment) {
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

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return interpreter.Call(this, args);
    }

    @Override
    public int arity() {
        return getParams().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FunValue funValue = (FunValue) o;

        return new EqualsBuilder().append(name.getSymbol(), funValue.name.getSymbol()).append(paramsAsString(), funValue.paramsAsString()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name.getSymbol()).append(paramsAsString()).toHashCode();
    }

    public List<String> paramsAsString() {
        return this.params.stream().map(Identifier::getSymbol).toList();
    }
}
