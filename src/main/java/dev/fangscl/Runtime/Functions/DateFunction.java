package dev.fangscl.Runtime.Functions;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;

import java.time.LocalDate;
import java.util.List;

public class DateFunction implements Callable {
    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return LocalDate.now().toString();
    }
}
