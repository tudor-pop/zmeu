package io.zmeu.TypeChecker.Types;

import io.zmeu.Frontend.Parser.NodeType;

public sealed class ReferenceType extends Type permits SchemaType {

    public ReferenceType(String typeName) {
        super(typeName);
        this.kind = NodeType.Type;
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
