package dev.fangscl.Runtime.Functions.Numeric;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;

import java.text.MessageFormat;
import java.util.List;

public class StringCastFunction implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        if (args.size() > 1) {
            throw new RuntimeException(MessageFormat.format("Too many arguments: {0}", args.size()));
        }
        var value = args.get(0);
        if (!(value instanceof String)) {
            return String.valueOf(value);
        }

        throw new RuntimeException("Invalid argument");
    }
}
