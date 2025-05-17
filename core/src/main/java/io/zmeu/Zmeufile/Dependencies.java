package io.zmeu.Zmeufile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Dependencies(
        List<Dependency> dependencies
) {
    public Dependencies(List<Dependency> dependencies) {
        this.dependencies = Objects.requireNonNullElseGet(dependencies, ArrayList::new);
    }
}
