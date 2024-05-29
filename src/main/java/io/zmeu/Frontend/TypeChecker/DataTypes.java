package io.zmeu.Frontend.TypeChecker;

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

    protected DataTypes(String value) {
        this.value = value;
    }

    protected DataTypes() {
        value = null;
    }
}
