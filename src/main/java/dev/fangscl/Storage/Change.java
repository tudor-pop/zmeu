package dev.fangscl.Storage;

import lombok.Getter;

public enum Change {
    CHANGE("±", "@|yellow %s|@"), REMOVE("-", "@|red %s|@"), ADD("+", "@|green %s|@");
    @Getter
    private final String symbol;
    @Getter
    private final String color;

    Change(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }
}
