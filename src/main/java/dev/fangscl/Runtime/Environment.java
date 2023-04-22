package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.DecimalValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.Values.StringValue;
import dev.fangscl.Runtime.exceptions.VarExistsException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Environment {
    @Nullable
    private final Environment parent;

    private final Map<String, RuntimeValue<?>> variables;

    public Environment(@Nullable Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<>(32);
    }

    public Environment() {
        this(null);
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

    /**
     * Assign a value to an existing variable
     * x = 10
     */
    public RuntimeValue assign(String varName, RuntimeValue value) {
        var env = this.resolve(varName);
        env.put(varName, value);
        return value;
    }

    public RuntimeValue evaluateVar(@Nullable String varName) {
        if (varName == null) {
            varName = "null";
        }
        return resolve(varName) // search the scope
                .get(varName); // return the value
    }

    /**
     * Search in the current scope for a variable, if found, return it; if not found, search in the parent scope
     *
     * @param varname
     * @return
     */
    private Environment resolve(String varname) {
        if (variables.containsKey(varname)) {
            return this;
        }
        if (parent == null) {
            throw new RuntimeException("Variable not found: " + varname);
        }
        return this.parent.resolve(varname);
    }

    private void put(String key, RuntimeValue value) {
        this.variables.put(key, value);
    }

    @Nullable
    public RuntimeValue get(String key) {
        return this.variables.get(key);
    }
}
