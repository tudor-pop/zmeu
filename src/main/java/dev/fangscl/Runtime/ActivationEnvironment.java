package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.RuntimeValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Storage for local variables and parameters during a function call
 * It iterates over the list of parameters and assign an argument value on each iteration
 */
public class ActivationEnvironment extends Environment {
    public ActivationEnvironment(@Nullable Environment parent, List<Expression> params, List<RuntimeValue<Object>> args) {
        super(parent);
        for (var i = 0; i < params.size(); i++) {
            // for each named parameter, we save the argument into the activation record(env that the function uses to execute)
            var paramName = ((Identifier) params.get(i)).getSymbol();
            init(paramName, args.get(i));
        }
    }
}
