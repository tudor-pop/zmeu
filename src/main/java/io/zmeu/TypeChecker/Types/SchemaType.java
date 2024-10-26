package io.zmeu.TypeChecker.Types;

import io.zmeu.TypeChecker.TypeEnvironment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SchemaType extends ReferenceType {

    @Getter
    private final TypeEnvironment environment;

    public SchemaType(String typeName) {
        this(typeName, null);
    }

    public SchemaType(String typeName, @Nullable TypeEnvironment env) {
        super(typeName);
        this.environment = new TypeEnvironment(env);
    }

    public Type getProperty(@NotNull String fieldName) {
        return environment.lookup(fieldName);
    }

    public Type setProperty(@NotNull String fieldName, Type type) {
        return environment.init(fieldName, type);
    }


}