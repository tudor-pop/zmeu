package dev.fangscl.Runtime.Scope;

import dev.fangscl.Runtime.Values.RuntimeValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    @Nullable
    private Scope parent;
    private Map<String, RuntimeValue> variables;

    public Scope(@Nullable Scope parent) {
        this.parent = parent;
        variables = new HashMap<>(32);
    }
    public Scope() {
        this(null);
    }

    /**
     * Declare a new variable.
     * var a = 2
     * var b = 3
     * @param varname
     * @param value
     * @return the value value
     */
    public RuntimeValue declareVar(String varname, RuntimeValue value) {
        if (variables.containsKey(varname)) {
            throw new RuntimeException("Variable is already declared: " + varname);
        }
        this.variables.put(varname, value);
        return value;
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

    public RuntimeValue evaluateVar(String varname) {
        return resolve(varname) // search the scope
                .variables.get(varname); // return the value
    }

    /**
     * Search in the current scope for a variable, if found, return it; if not found, search in the parent scope
     * @param varname
     * @return
     */
    private Scope resolve(String varname) {
        if (variables.containsKey(varname)) {
            return this;
        }
        if (parent == null) {
            throw new RuntimeException("Variable not found: " + varname);
        }
        return this.parent.resolve(varname);
    }
}
