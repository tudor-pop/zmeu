package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Environment.Environment;
import dev.fangscl.Runtime.Environment.IEnvironment;
import org.jetbrains.annotations.Nullable;

public class InterpreterEnvironment implements IEnvironment {
    private final Environment env;
    private final Environment global = new Environment();

    public InterpreterEnvironment(Environment env) {
        this.env = env;
    }

    @Override
    public Object assign(String varName, Object value) {
        return env.assign(varName, value);
    }

    @Override
    public Object lookup(@Nullable String varName) {
        return env.lookup(varName);
    }

    @Override
    public Object lookup(@Nullable Object varName) {
        return env.lookup(varName);
    }

    @Override
    public IEnvironment getParent() {
        return env.getParent();
    }

    @Override
    public @Nullable Object get(String key) {
        return env.get(key);
    }

    @Override
    public Object init(String name, Object value) {
        return env.init(name, value);
    }

}
