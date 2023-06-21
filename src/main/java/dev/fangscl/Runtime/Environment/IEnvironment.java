package dev.fangscl.Runtime.Environment;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IEnvironment {
    Object assign(String varName, Object value);

    Object lookup(@Nullable String varName);

    Object lookup(@Nullable Object varName);

    IEnvironment getParent();

    /**
     * Return the environment after going through n parents
     */
    default IEnvironment ancestor(Integer hops) {
        IEnvironment environment = this;
        if (hops == null) {
            // if number of hops is not defined we are not in a local scope so we try to find
            // the variable in the global scope. Else we know exactly how many parents we need to go up the chain
//            for (var parent = getParent(); parent != null; parent = parent.getParent()){
//                environment = parent;
//            }
            return environment;
        }
        for (int i = 0; i < hops; i++) {
            var parent1 = environment.getParent();
            if (parent1 == null) break;

            environment = parent1;
        }
        return environment;
    }

    @Nullable Object get(String key);

    default Object init(String name, int value) {
        return init(name, Integer.valueOf(value));
    }

    default Object init(String name, double value) {
        return init(name, Double.valueOf(value));
    }

    default Object init(String name, float value) {
        return init(name, Float.valueOf(value));
    }

    default Object init(String name, String value) {
        return init(name, (Object) value);
    }

    default Object init(String name, boolean value) {
        return init(name, Boolean.valueOf(value));
    }

    default Object init(Identifier name, Object value) {
        return init(name.getSymbol(), value);
    }

    Object init(String name, Object value);

    default Object assign(String symbol, Object right, Integer hops) {
        return ancestor(hops).assign(symbol, right);
    }

    default Object lookup(String symbol, Integer hops) {
        return ancestor(hops).lookup(symbol);
    }

}
