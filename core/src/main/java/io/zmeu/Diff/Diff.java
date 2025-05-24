package io.zmeu.Diff;

import io.zmeu.api.resource.Resource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
     *
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

        base = base == null ? right : base;
        if (right != null) {
            /**
             * accept right/theirs/cloud changes. Any undeclared properties in the state (like unique cloud ids)
             * will be set on the base since they are probably already out of date in the base state
             * */
            ignoreNullBeanUtils.copyProperties(base, right);
        } else {
            base = null;
        }
        // Preserve cloud-managed properties explicitly.
        // Src fields must get the cloud values because they are not explicitly set in code but rather set by the cloud provider(read only properties)
        if (base != null && left != null) {
            DiffUtils.updateReadOnlyProperties(base, left);
        }

        var diff = this.javers.compare(base, left);


        if (base == null) {
            base = left;
        } else if (left != null) {
            ignoreNullBeanUtils.copyProperties(base, left);
        } else { // on object removed (src doesn't exist because it was removed) create an empty object of the same type
            base = base.getClass().newInstance();
        }

        return new MergeResult(diff.getChanges(), base);
    }

}
