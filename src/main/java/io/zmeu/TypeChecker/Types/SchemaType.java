package io.zmeu.TypeChecker.Types;

public final class SchemaType extends ReferenceType {

    private SchemaType(String typeName) {
        super(typeName);
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
