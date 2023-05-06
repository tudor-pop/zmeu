package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.*;
import dev.fangscl.Runtime.exceptions.VarExistsException;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Environment implements IEnvironment {
    @Nullable
    private final Environment parent;

    private final Map<String, RuntimeValue<?>> variables;

    public Environment(@Nullable Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<>(32);
    }

    public Environment(@Nullable Environment parent, Map<String, RuntimeValue<?>> variables) {
        this.parent = parent;
        this.variables = variables;
    }

    public Environment(Map<String, RuntimeValue<?>> variables) {
        this.parent = null;
        this.variables = variables;
    }

    public Environment(Map.Entry<String, RuntimeValue<?>>... variables) {
        this.parent = null;
        this.variables = new HashMap<>();
        for (var variable : variables) {
            this.variables.put(variable.getKey(), variable.getValue());
        }
    }

    public Environment() {
        this(new HashMap<>());
    }

    /**
     * Declare a new variable with given name and value.
     * var a = 2
     * var b = 3
     */
    public RuntimeValue init(String name, RuntimeValue value) {
        if (variables.containsKey(name)) {
            throw new VarExistsException(name);
        }
        this.put(name, value);
        return value;
    }

    public RuntimeValue init(String name, int value) {
        return init(name, IntegerValue.of(value));
    }

    public RuntimeValue init(String name, double value) {
        return init(name, DecimalValue.of(value));
    }

    public RuntimeValue init(String name, float value) {
        return init(name, DecimalValue.of(value));
    }

    public RuntimeValue init(String name, String value) {
        return init(name, StringValue.of(value));
    }

    public RuntimeValue init(String name, boolean value) {
        return init(name, BooleanValue.of(value));
    }

    public RuntimeValue init(Identifier name, RuntimeValue value) {
        return init(name.getSymbol(), value);
    }

    /**
     * Assign a value to an existing variable
     * x = 10
     */
    @Override
    public RuntimeValue assign(String varName, RuntimeValue value) {
        var env = this.resolve(varName);
        env.put(varName, value);
        return value;
    }

    @Override
    public RuntimeValue lookup(@Nullable String varName) {
        if (varName == null) {
            varName = "null";
        }
        return resolve(varName) // search the scope
                .get(varName); // return the value
    }

    @Override
    public RuntimeValue lookup(@Nullable RuntimeValue<String> varName) {
        return lookup(varName.getRuntimeValue());
    }

    /**
     * Search in the current scope for a variable, if found, return it; if not found, search in the parent scope
     *
     * @param symbol
     * @return
     */
    private Environment resolve(String symbol, String error) {
        if (variables.containsKey(symbol)) {
            return this;
        }
        if (parent == null) {
            throw new NotFoundException(error);
        }
        return this.parent.resolve(symbol, error);
    }

    private Environment resolve(String symbol) {
        return resolve(symbol, "Variable not found: ");
    }

    private void put(String key, RuntimeValue value) {
        this.variables.put(key, value);
    }

    @Nullable
    public RuntimeValue get(String key) {
        return this.variables.get(key);
    }

    public RuntimeValue lookup(String symbol, String error) {
        return resolve(symbol, error) // search the scope
                .get(symbol);
    }
}
