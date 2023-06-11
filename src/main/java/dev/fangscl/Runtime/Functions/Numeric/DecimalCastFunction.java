package dev.fangscl.Runtime.Functions.Numeric;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;

import java.util.List;

public class DecimalCastFunction implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Object arg = args.get(0);
        if (arg instanceof String s) {
            return Double.parseDouble(s);
        } else if (arg instanceof Float d){
            return Double.valueOf(d.toString());
        }else if (arg instanceof Number d) {
            return d.doubleValue();
        }
        throw new RuntimeException("Argument '%s' can't be converted to int".formatted(arg));
    }
}
