package io.zmeu.Diff;

import lombok.Getter;

@Getter
public enum Change {
    NO_OP("", ""),CHANGE("~", "@|yellow %s|@"), REMOVE("-", "@|red %s|@"), ADD("+", "@|green %s|@");
    private final String symbol;
    private final String color;

    Change(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }

    public String coloredOperation() {
        return color.formatted(symbol);
    }

    public String color(String string) {
        return color.formatted(string);
    }
}
