package io.zmeu.TypeChecker.Types;

import io.zmeu.TypeChecker.TypeEnvironment;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SchemaType extends ReferenceType {
    public static SchemaType baseSchema = new SchemaType("base");

    private final TypeEnvironment environment;
    private SchemaType extended = baseSchema;

    public SchemaType(String typeName) {
        this(typeName, null, null);
    }

    public SchemaType(String typeName, SchemaType extended, @Nullable TypeEnvironment env) {
        super(typeName);
        this.extended = extended;
        this.environment = new TypeEnvironment(env);
    }

    public SchemaType(String typeName, @Nullable TypeEnvironment env) {
        this(typeName, baseSchema, env);
    }

    public SchemaType(String typeName, SchemaType extended) {
        this(typeName, extended, null);
    }

    public Type getField(@NotNull String fieldName) {
        return environment.lookup(fieldName);
    }

}