package io.zmeu.types;

public enum Types {
    String("String"), Number("Number"), Boolean("Boolean"), Null("null");
    private String value;

    private Types(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
