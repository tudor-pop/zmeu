package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ValueType extends Type {
    public static ValueType String = new ValueType("String");
    public static ValueType Number = new ValueType("Number");
    public static ValueType Boolean = new ValueType("Boolean");
    public static ValueType Null = new ValueType("Null");

    private ValueType() {
        super();
    }

    private ValueType(String value) {
        super();
        setValue(value);
    }

    public static ValueType of(String value) {
        var valueType = new ValueType();
        return switch (value) {
            case "Boolean", "Number", "String" -> {
                valueType.setValue(value);
                yield valueType;
            }
            default -> null;
        };
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
