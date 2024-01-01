package dev.fangscl.Backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Resource {
    private String name;
    @Builder.Default
    protected Map<String, Object> properties = new HashMap<>();

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Resource() {
        properties = new HashMap<>();
    }
}
