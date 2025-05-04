package io.zmeu.Engine;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;

import java.lang.reflect.Field;

@Slf4j
public class JaversUtils {
    // Method to map snapshot properties to an instance of the target class
    public static <T> T mapSnapshotToObject(@NonNull CdoSnapshot snapshot, Class<T> targetClass) {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();

            // Set the ID from the GlobalId
            Field idField = getIdField(targetClass);
            if (idField != null) {
                var globalId = (InstanceId) snapshot.getGlobalId();
                var id = globalId.getCdoId().toString();
                idField.setAccessible(true);
                idField.setAccessible(true);
                idField.set(instance, id);
            }

            snapshot.getState().forEachProperty((propertyName, propertyValue) -> {
                try {
                    var field = targetClass.getDeclaredField(propertyName);
                    field.setAccessible(true);
                    field.set(instance, propertyValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                    // Ignore fields that are not in the target class
                }
            });
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map snapshot to object", e);
        }
    }

    private static Field getIdField(Class<?> clazz) {
        // Search for @Id annotated field in the class hierarchy
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null; // No @Id field found
    }
}
