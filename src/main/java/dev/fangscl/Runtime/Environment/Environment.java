package dev.fangscl.Runtime.Environment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Resources.Resource;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import dev.fangscl.Runtime.exceptions.VarExistsException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Environment implements IEnvironment {
    @Nullable
    @Getter
    private final Environment parent;

    @Getter
    @JsonIgnoreProperties("variables")
    private final Map<String, Object> variables;

    public Environment(@Nullable Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<>(8);
    }

    public Environment(@Nullable Environment parent, Map<String, Object> variables) {
        this(parent);
        this.variables.putAll(variables);
    }

    public Environment(@Nullable Environment parent, Resource variables) {
        this(parent);
        for (Field field : variables.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.variables.put(field.getName(), field.get(field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Environment(Map<String, Object> variables) {
        this.parent = null;
        this.variables = variables;
    }

    public Environment() {
        this(new HashMap<>());
    }

    /**
     * Declare a new variable with given name and value.
     * var a = 2
     * var b = 3
     */
    public Object init(String name, Object value) {
        if (variables.containsKey(name)) {
            throw new VarExistsException(name);
        }
        this.put(name, value);
        return value;
    }

    public Object init(String name, int value) {
        return init(name, Integer.valueOf(value));
    }

    public Object init(String name, double value) {
        return init(name, Double.valueOf(value));
    }

    public Object init(String name, float value) {
        return init(name, Float.valueOf(value));
    }

    public Object init(String name, String value) {
        return init(name, (Object) value);
    }

    public Object init(String name, boolean value) {
        return init(name, Boolean.valueOf(value));
    }

    public Object init(Identifier name, Object value) {
        return init(name.getSymbol(), value);
    }

    /**
     * Assign a value to an existing variable
     * x = 10
     */
    @Override
    public Object assign(String varName, Object value) {
        var env = this.resolve(varName);
        env.put(varName, value);
        return value;
    }

    @Override
    public Object lookup(@Nullable String varName) {
        if (varName == null) {
            varName = "null";
        }
        return resolve(varName) // search the scope
                .get(varName); // return the value
    }

    @Override
    public Object lookup(@Nullable Object varName) {
        return lookup(varName);
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
            throw new NotFoundException(error, symbol);
        }
        return this.parent.resolve(symbol, error);
    }

    private Environment resolve(String symbol) {
        return resolve(symbol, "Variable not found: ");
    }

    private void put(String key, Object value) {
        this.variables.put(key, value);
    }

    @Override
    public Object get(String key) {
        return this.variables.get(key);
    }

    public Object lookup(String symbol, String error) {
        return resolve(symbol, error) // search the scope
                .get(symbol);
    }

    public boolean hasVar(String symbol) {
        return getVariables().keySet().contains(symbol);
    }
}
