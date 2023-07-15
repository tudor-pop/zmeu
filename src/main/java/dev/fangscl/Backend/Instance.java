package dev.fangscl.Backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Instance {
    private String name;
    private List<String> dependencies;
    private Map<String, Object> properties;
}
