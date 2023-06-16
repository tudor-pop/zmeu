package dev.fangscl.Resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vm extends Resource {
    private String name;
    private String imageId;
    private Integer minCount;
    private Integer maxCount;
    @JsonAlias({"instanceId", "id"})
    private String id;

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
        if (minCount == null) {
            minCount = maxCount;
        }
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
        if (maxCount == null) {
            maxCount = minCount;
        }
    }
}
