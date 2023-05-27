package dev.fangscl.Runtime;

import java.util.List;

public interface Callable {
    Object call(Interpreter interpreter, List<Object> args);
}
