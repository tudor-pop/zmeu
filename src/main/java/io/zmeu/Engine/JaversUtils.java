package io.zmeu.Engine;

import lombok.NonNull;
import org.javers.core.metamodel.object.CdoSnapshot;

public class JaversUtils {
    // Method to map snapshot properties to an instance of the target class
    public static <T> T mapSnapshotToObject(@NonNull CdoSnapshot snapshot, Class<T> targetClass) {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            snapshot.getState().forEachProperty((propertyName, propertyValue) -> {
                try {
                    var field = targetClass.getDeclaredField(propertyName);
                    field.setAccessible(true);
                    field.set(instance, propertyValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Ignore fields that are not in the target class
                }
            });
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map snapshot to object", e);
        }
    }
}
