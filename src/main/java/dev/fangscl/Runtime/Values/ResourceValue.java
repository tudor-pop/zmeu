package dev.fangscl.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.fangscl.Runtime.Environment.Environment;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResourceValue {
    @JsonIgnore
    private Environment args;
    private String name;

    private ResourceValue(String name, Environment parent) {
        this.name = name;
        this.args = parent;
    }

    private ResourceValue(String e) {
        this(e, new Environment());
    }

    public String getRuntimeValue() {
        return name;
    }

    public static Object of(String string) {
        return ResourceValue.of(string, new Environment());
    }

    public static Object of(String string, Environment environment) {
        return new ResourceValue(string, environment);
    }

    public Object argVal(String name) {
        return args.get(name);
    }

    @Nullable
    public String name() {
        return name;
    }

    public Object assign(String varName, Object value) {
        return args.assign(varName, value);
    }

    public Object lookup(@Nullable String varName) {
        return args.lookup(varName);
    }

    public Object lookup(@Nullable Object varName) {
        return args.lookup(varName);
    }

    public @Nullable Object get(String key) {
        return args.get(key);
    }

    public Object init(String name, Object value) {
        return args.init(name, value);
    }

    public record Data(String name, Map<String, Object> args) {
    }

    public Data asData() {
        var entries = args.getVariables().entrySet();
        var args = new HashMap<String, Object>(entries.size());
        for (var it : entries) {
            args.put(it.getKey(), it.getValue());
        }
        return new Data(this.name(), args);
    }

}
