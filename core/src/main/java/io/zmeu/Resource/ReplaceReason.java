package io.zmeu.Resource;

import java.util.HashSet;
import java.util.Set;

public class ReplaceReason {
    private Set<String> immutablePropertyChange = new HashSet<>();

    public void addImmutableProperty(String name) {
        if (immutablePropertyChange == null) {
            immutablePropertyChange = new HashSet<>();
        }
        immutablePropertyChange.add(name);
    }

    public void setImmutableProperties(Set<String> immutablePropertyChange) {
        this.immutablePropertyChange = immutablePropertyChange;
    }

    public boolean isImmutable(String name) {
        return immutablePropertyChange.contains(name);
    }

    public boolean hasImmutableProperties() {
        return !immutablePropertyChange.isEmpty();
    }
}
