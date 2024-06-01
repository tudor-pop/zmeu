package io.zmeu.Frontend.TypeChecker.Types;

import lombok.Getter;
import lombok.Setter;

public class DataTypes {
    public static DataTypes String = new DataTypes("String");
    public static DataTypes Number = new DataTypes("Number");
    public static DataTypes Boolean = new DataTypes("Boolean");
    public static DataTypes Null = new DataTypes("Null");

    @Getter
    @Setter
    private String value;

    public boolean hasValue() {
        return value != null;
    }

    protected DataTypes(String value) {
        this.value = value;
    }

    protected DataTypes() {
        value = null;
    }

    public static DataTypes valueOf(String symbol) {
        return switch (symbol) {
            case "String" -> String;
            case "Number" -> Number;
            case "Boolean" -> Boolean;
            case "Null" -> Null;
            default -> {
                if (symbol.startsWith("fun")) {
                    FunType fun = FunStore.getFun(symbol);
                    if (fun != null) {
                        yield fun;
                    } else {
                        yield FunStore.setFun(symbol, FunType.valueOf(symbol));
                    }
                }
                throw new IllegalArgumentException("Invalid symbol: " + symbol);
            }
        };
    }
}
