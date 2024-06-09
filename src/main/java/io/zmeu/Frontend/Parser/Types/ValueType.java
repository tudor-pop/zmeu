package io.zmeu.Frontend.Parser.Types;

import lombok.EqualsAndHashCode;

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
        return switch (value) {
            case "Boolean"-> Boolean;
            case "String"-> String;
            case "Number"-> Number;
            case "Null"-> Null;
            default -> null;
        };
    }

    @Override
    public java.lang.String toString() {
        return getValue();
    }


}
