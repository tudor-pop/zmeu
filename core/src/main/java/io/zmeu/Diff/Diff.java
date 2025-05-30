package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
@Log4j2
public class Diff {
    @Getter
    private Javers javers;
    private final IgnoreNullBeanUtilsBean ignoreNullBeanUtils = new IgnoreNullBeanUtilsBean();

    @SneakyThrows
    public Diff(Javers javers) {
        this();
        this.javers = javers;
    }

    public Diff() {

    }

    /**
     * @param base  javers state stored in the database
     * @param left  source code state
     * @param right state read from the cloud like a VM
     * @return
     */
    @SneakyThrows
    public MergeResult merge(@Nullable Resource base, Resource left, @Nullable Resource right) {
        DiffUtils.validate(base);
        DiffUtils.validate(left);
        DiffUtils.validate(right);
        if (left != null && right == null && base != null) {// missing from cloud but added in src
            left.setId(base.getId()); // src and local ids should be the same
            var diff = this.javers.compare(null, left);
            return new MergeResult(diff.getChanges(), left);
        }

        base = base == null ? right : base;

        if (base != right) {
            if (right != null) {
                ignoreNullBeanUtils.copyProperties(base, right); // update base with cloud
            }
        }
        if (left != null && right != null) {
            DiffUtils.updateImmutableProperties(right, left);
        }

        // Preserve cloud-managed properties explicitly.
        // Src fields must get the cloud values because they are not explicitly set in code but rather set by the cloud provider(read only properties)
        if (base != null && right != null && base != right) {
            DiffUtils.updateImmutableProperties(right, base);
        }
        if (left != null && base != null) { // update src id if present in localstate. Else will be saved to state from src
            left.setId(base.getId());
        }
        var diff = this.javers.compare(base, left);
        if (left != null && base != null) {
            if (!StringUtils.equals(base.resourceName().getName(), left.resourceName().getName())) {
                left.resourceName().setRenamedFrom(base.resourceName().getName());
            }
            left.setId(base.getId());
            ignoreNullBeanUtils.copyProperties(base, left);
        } else if (base == null && left != null) { // on object removed (src doesn't exist because it was removed) create an empty object of the same type
            base = left;
        }

        return new MergeResult(diff.getChanges(), base);
    }

}
