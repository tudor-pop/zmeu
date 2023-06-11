package dev.fangscl.Runtime.Functions.Numeric;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;

import java.util.List;

public class IntFunction implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Object arg = args.get(0);
        if (arg instanceof String s) {
            return Integer.parseInt(s);
        } else if (arg instanceof Number d) {
            return d.intValue();
        }
        throw new RuntimeException("Argument '%s' can't be converted to int".formatted(arg));
    }
}
