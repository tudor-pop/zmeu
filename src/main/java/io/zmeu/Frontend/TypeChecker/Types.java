package io.zmeu.Frontend.TypeChecker;

public enum Types {
    String("String"), Number("Number"), Boolean("Boolean"), Null("null");
    private final String value;

    Types(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
