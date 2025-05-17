package io.zmeu.Diff;

import lombok.Getter;

@Getter
public enum ResourceChange {
    NO_OP("", ""),
    CHANGE("~", "@|yellow %s|@"),
    REMOVE("-", "@|red %s|@"),
    ADD("+", "@|green %s|@"),
    WHITE("->", "@|white %s|@");
    private final String symbol;
    private final String color;

    ResourceChange(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }

    public String toColor() {
        return color.formatted(symbol);
    }

    public String color(String string) {
        return color.formatted(string);
    }
}
