package dev.fangscl.Runtime.Functions;

import dev.fangscl.Runtime.Callable;
import dev.fangscl.Runtime.Interpreter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PrintFunction implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        if (args.size() == 1) {
            var result = args.get(0);
            System.out.print(result);
            return result;
        }
        var result = StringUtils.join(args);
        System.out.print(result);
        return result;
    }
}
