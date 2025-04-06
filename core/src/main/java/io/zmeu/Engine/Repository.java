package io.zmeu.Engine;

import io.zmeu.Diff.JaversFactory;
import org.javers.core.Javers;

public class Repository {
    public Javers javers;

    public Repository(Javers javers) {
        this.javers = javers;
    }
}
