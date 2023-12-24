package dev.fangscl.Backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Resource {
    private String name;
    @Builder.Default
    private String type="Cluster";
    private Map<String, Object> properties;
}
