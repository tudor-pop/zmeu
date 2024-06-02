package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ValueType extends Type {
    private String value;

    private ValueType() {
        super();
    }

    public static ValueType of(String value) {
        ValueType valueType = new ValueType();
        return switch (value){
            case "Boolean", "Number", "String" -> {
                valueType.setValue(value);
                yield valueType;
            }
            default -> null;
        };
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
