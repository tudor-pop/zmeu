package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.DecimalValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.exceptions.VarExistsException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Environment {
    @Nullable
    private final Environment parent;
    @Getter
    private final Map<String, RuntimeValue> variables;

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
    public RuntimeValue declareVar(String name, RuntimeValue value) {
        if (variables.containsKey(name)) {
            throw new VarExistsException(name);
        }
        this.variables.put(name, value);
        return value;
    }

    public RuntimeValue declareVar(String name, int value) {
        return declareVar(name, IntegerValue.of(value));
    }

    public RuntimeValue declareVar(String name, double value) {
        return declareVar(name, DecimalValue.of(value));
    }

    /**
     * Assign a value to an existing variable
     *
     * @param varname
     * @param value
     * @return
     */
    public RuntimeValue assignVar(String varname, RuntimeValue value) {
        var env = this.resolve(varname);
        env.variables.put(varname, value);
        return value;
    }

    public RuntimeValue evaluateVar(@Nullable String varname) {
        if (varname == null) varname = "null";
        return resolve(varname) // search the scope
                .variables.get(varname); // return the value
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
}
