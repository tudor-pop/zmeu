package io.zmeu.TypeChecker.Types;

public sealed class ReferenceType extends Type permits ResourceType, SchemaType {

    public ReferenceType(String typeName) {
        super(typeName);
    }

    public static ReferenceType of(String typeName) {
        return new ReferenceType(typeName);
    }

    @Override
    public String toString() {
        return super.getValue();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
