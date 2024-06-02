package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ReferenceType extends Type {
    private String typeName;// TODO

    private ReferenceType(String typeName) {
        super();
        this.typeName = typeName;
    }

    public static ReferenceType of(String typeName) {
        return new ReferenceType(typeName);
    }

    @Override
    public String value() {
        return typeName;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
