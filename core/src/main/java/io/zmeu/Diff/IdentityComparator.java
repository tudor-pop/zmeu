package io.zmeu.Diff;

import io.zmeu.api.resource.Identity;
import org.javers.core.diff.custom.CustomValueComparator;

import java.util.Objects;

public class IdentityComparator implements CustomValueComparator<Identity> {
    @Override
    public boolean equals(Identity a, Identity b) {
        if (a.getId() != null && b.getId() != null) {
            return a.getId().equals(b.getId());
        }
        return Objects.equals(a.getName(), b.getName());
    }

    @Override
    public String toString(Identity value) {
        if (value.getId() != null) {
            return value.getId();
        }
        return value.getName();
    }
}
