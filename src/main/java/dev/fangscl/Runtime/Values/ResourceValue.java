package dev.fangscl.Runtime.Values;

import dev.fangscl.Runtime.Environment;
import dev.fangscl.Runtime.IEnvironment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ResourceValue implements IEnvironment {
    private Environment environment;
    private String name;
    private List<Object> args;

    private ResourceValue(String name, List<Object> args, Environment environment) {
        this.name = name;
        this.args = args;
        this.environment = environment;
    }

    private ResourceValue(String e) {
        this(e, Collections.emptyList(), new Environment());
    }

    public String getRuntimeValue() {
        return name;
    }

    public static Object of(String name, List<Object> params, Environment environment) {
        return new ResourceValue(name, params, environment);
    }

    public static Object of(List<Object> params, Environment environment) {
        return new ResourceValue(null, params, environment);
    }

    public static Object of(String string) {
        return ResourceValue.of(string, Collections.emptyList(), new Environment());
    }

    public static Object of(String string, Environment environment) {
        return ResourceValue.of(string, Collections.emptyList(), environment);
    }


    @Nullable
    public String name() {
        return name;
    }

    @Override
    public Object assign(String varName, Object value) {
        return environment.assign(varName, value);
    }

    @Override
    public Object lookup(@Nullable String varName) {
        return environment.lookup(varName);
    }

    @Override
    public Object lookup(@Nullable Object varName) {
        return environment.lookup(varName);
    }

    @Override
    public @Nullable Object get(String key) {
        return environment.get(key);
    }

    public record Data(String name, Map<String, Object> args) {
    }

    public Data asData() {
        var entries = environment.getVariables().entrySet();
        var args = new HashMap<String, Object>(entries.size());
        for (var it : entries) {
            args.put(it.getKey(), it.getValue());
        }
        return new Data(this.name(), args);
    }

}
