package dev.fangscl.Runtime.Functions.Cast;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;

import java.util.List;

public class BooleanCastFunction implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        if (args.size() == 0) {
            return false;
        }
        Object arg = args.get(0);
        if (arg == null) {
            return false;
        }
        if (arg instanceof String s) {
            if (s.equalsIgnoreCase("yes")) {
                return true;
            } else if (s.equalsIgnoreCase("no")) {
                return false;
            }
            return Boolean.parseBoolean(s);
        }
        throw new RuntimeException("Argument '%s' can't be converted to int".formatted(arg));
    }
}
