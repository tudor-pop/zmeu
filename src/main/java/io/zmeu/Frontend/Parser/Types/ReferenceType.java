package io.zmeu.Frontend.Parser.Types;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ReferenceType extends Type {

    private ReferenceType(String typeName) {
        super(typeName);
    }

    public static ReferenceType of(String typeName) {
        return new ReferenceType(typeName);
    }

}
