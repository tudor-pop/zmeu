package io.zmeu.api;

import com.github.zafarkhaja.semver.Version;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Schema {
    private String description;
    private Version version;
    private List<Attribute> attributes = new ArrayList<>();

    protected void add(Attribute attribute) {
        attributes.add(attribute);
    }
}
