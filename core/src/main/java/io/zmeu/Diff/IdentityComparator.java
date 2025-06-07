package io.zmeu.Diff;

import io.zmeu.Resource.Identity;
import org.javers.core.diff.custom.CustomValueComparator;

import java.util.Objects;

public class IdentityComparator implements CustomValueComparator<Identity> {
    @Override
    public boolean equals(Identity a, Identity b) {
        return Objects.equals(a.getName(), b.getName());
    }

    @Override
    public String toString(Identity value) {
        return value.getName();
    }
}
