package io.zmeu.TypeChecker.Types;

import io.zmeu.TypeChecker.TypeEnvironment;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SchemaType extends ReferenceType {
    private final TypeEnvironment environment;
    private final SchemaType extended;

    public SchemaType(String typeName) {
        this(typeName, null, null);
    }

    public SchemaType(String typeName, SchemaType extended, @Nullable TypeEnvironment env) {
        super(typeName);
        this.extended = extended;
        this.environment = new TypeEnvironment(env);
    }

    public Type getField(@NotNull String fieldName) {
        return environment.lookup(fieldName);
    }

}