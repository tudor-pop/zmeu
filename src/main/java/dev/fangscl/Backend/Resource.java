package dev.fangscl.Backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private String name;
    private String schema;
    private String type;
    private List<Instance> instances;
    private Map<String, Object> properties;
}
