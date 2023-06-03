package dev.fangscl.Runtime.Values;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NullValue implements RuntimeValue<Object> {
    private static final NullValue value = new NullValue();

    public NullValue() {
    }

    @Override
    @Nullable
    @JsonIgnore
    public Object getRuntimeValue() {
        return value;
    }

    public static NullValue of() {
        return value;
    }
}
