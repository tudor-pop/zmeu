package io.zmeu.TypeChecker;

import io.zmeu.Resources.Resource;
import io.zmeu.Runtime.Environment.Environment;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/*
* Mapping from names to types
* In type theory is called Gamma
*
* */
@Log4j2
public class TypeEnvironment extends Environment {
    public TypeEnvironment(@Nullable Environment parent) {
        super(parent);
    }

    public TypeEnvironment(@Nullable Environment parent, Map<String, Object> variables) {
        super(parent, variables);
    }

    public TypeEnvironment(@Nullable Environment parent, Resource variables) {
        super(parent, variables);
    }

    public TypeEnvironment(Map<String, Object> variables) {
        super(variables);
    }

    public TypeEnvironment() {
    }
}
