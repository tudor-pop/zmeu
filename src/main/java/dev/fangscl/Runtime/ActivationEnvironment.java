package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Storage for local variables and parameters during a function call
 * It iterates over the list of parameters and assign an argument value on each iteration
 */
public class ActivationEnvironment extends Environment {

    /**
     * @param parent Set to the environment from where we're being called to obtain dynamic scope.
     *               Set to the environment where the function was declared to obtain the static scope
     * @param params
     * @param args
     */
    public ActivationEnvironment(@Nullable Environment parent, List<Expression> params, List<Object> args) {
        super(parent);
        for (var i = 0; i < params.size(); i++) {
            // for each named parameter, we save the argument into the activation record(env that the function uses to execute)
            var paramName = ((Identifier) params.get(i)).getSymbol();
            init(paramName, args.get(i));
        }
    }
}
