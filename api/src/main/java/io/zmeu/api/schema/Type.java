package io.zmeu.api.schema;

import lombok.Getter;

@Getter
public enum Type {
    String("String"), Number("Number"), Boolean("Boolean");
    private final String value;

    Type(String value) {
        this.value = value;
    }

}
