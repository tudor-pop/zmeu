package io.zmeu.Diff;

import io.zmeu.Plugin.Providers;
import io.zmeu.Resource.ReplaceReason;
import io.zmeu.Resource.Resource;
import io.zmeu.Utils.Reflections;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.Javers;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 *
 */
@Log4j2
public class Diff {
    @Getter
    private Javers javers;
    private final IgnoreNullBeanUtilsBean ignoreNullBeanUtils = new IgnoreNullBeanUtilsBean();
    private Providers providers;

    @SneakyThrows
    public Diff(Javers javers, Providers providers) {
        this();
        this.javers = javers;
        this.providers = providers;
    }

    public Diff() {

    }

    /**
     * @param base  database state
     * @param left  source code state
     * @param right state read from the cloud like a VM
     * @return
     */
    @SneakyThrows
    public MergeResult merge(@Nullable Resource base, Resource left, @Nullable Resource right) {
        DiffUtils.validate(base);
        DiffUtils.validate(left);
        DiffUtils.validate(right);

        var resourceMissing = isCloudResourceMissing(base, left, right);
        if (resourceMissing != null) return resourceMissing;
        var isNewResource = isNewResource(base, left, right);
        if (isNewResource != null) return isNewResource;

        base = base == null ? right : base;

        updateBaseWithCloudProperties(base, right);

        updateSourceResourceId(base, left);

        // this step detects replacement if immutable cloud property was changed in src, change the uuid of the local/src so that it's being replaced
        detectReplacement(left, right);

        // Preserve cloud-managed properties explicitly.
        // Src fields must get the cloud values because they are not explicitly set in code but rather set by the cloud provider(read only properties)
        if (base != null && right != null && base != right) {
            updateImmutableProperties(right, base);
            base.setReplace(right.getReplace());
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

    static void updateImmutableProperties(Resource source, Resource target) {
        var sourceProps = source.getProperties();
        var targetProps = target.getProperties();
        var beanUtils = BeanUtilsBean2.getInstance();
        var changedProperties = new HashSet<String>();
        Stream.of(targetProps.getClass().getDeclaredFields())
                .filter(Reflections::isImmutable)
                .forEach(field -> {
                    field.setAccessible(true);

                    try {
                        Field sourceField = sourceProps.getClass().getDeclaredField(field.getName());
                        sourceField.setAccessible(true);

                        Object sourceValue = sourceField.get(sourceProps);
                        Object targetValue = beanUtils.getProperty(targetProps, field.getName());

                        boolean isDifferent = targetValue != null && !Objects.equals(sourceValue, targetValue);
                        if (isDifferent) {
                            changedProperties.add(field.getName());
                        } else {
                            beanUtils.copyProperty(targetProps, field.getName(), sourceValue);
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Error copying immutable property: " + field.getName(), e);
                    }
                });
        if (!changedProperties.isEmpty()) {
            var replace = new ReplaceReason();
            replace.setImmutableProperties(changedProperties);
            source.setReplace(replace);
            target.setReplace(replace);
        }
    }

    private static void updateSourceResourceId(@Nullable Resource base, Resource left) {
        if (base != null && left != null) { // update src id if present in state
            left.setId(base.getId()); // must be done before detecting resource replacement in the next step
        } else if (base == null && left != null) {
            left.setId(UUID.randomUUID());
        }
    }

    /**
     * Base state needs to get the cloud properties updated
     */
    private void updateBaseWithCloudProperties(@Nullable Resource base, @Nullable Resource right) throws IllegalAccessException, InvocationTargetException {
        if (base != right && right != null) {// uuid doesn't come from cloud neither resource name so let's keep the state values that were generated when it was saved to state
            right.setId(base.getId());
            right.setIdentity(base.getIdentity());
            ignoreNullBeanUtils.copyProperties(base, right); // update base with cloud
        }
    }

    /**
     * Cloud resource was deleted but it's present in state and src
     */
    private @Nullable MergeResult isCloudResourceMissing(@Nullable Resource base, Resource left, @Nullable Resource right) {
        if (left != null && base != null && right == null) {// missing from cloud but added in src
            // src and local ids should be the same but
            // the names should not be the same because src could rename a resource and we don't want it to be replaced
            updateSourceResourceId(base, left);

            var diff = this.javers.compare(null, left);
            return new MergeResult(diff.getChanges(), left);
        }
        return null;
    }

    /**
     * Cloud resource was deleted but it's present in state and src
     */
    private @Nullable MergeResult isNewResource(@Nullable Resource base, Resource left, @Nullable Resource right) {
        if (left != null && base == null && right == null) {// not stored in base or cloud
            // src and local ids should be the same but
            // the names should not be the same because src could rename a resource and we don't want it to be replaced
            updateSourceResourceId(base, left);

            var diff = this.javers.compare(null, left);
            // id is only needed for the compare(throws without it) but is not needed when saved in the database or else is considered an existing resource
            left.setId(null);
            return new MergeResult(diff.getChanges(), left);
        }
        return null;
    }

    private static void detectReplacement(Resource left, @Nullable Resource right) {
        if (left != null && right != null) {
            updateImmutableProperties(right, left);
        }
    }

}
