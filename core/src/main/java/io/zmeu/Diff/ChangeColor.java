package io.zmeu.Diff;

import lombok.Getter;

@Getter
public enum ChangeColor {
    NO_OP("", ""),
    CHANGE("~", "@|yellow %s|@"),
    REMOVE("-", "@|red %s|@"),
    ADD("+", "@|green %s|@"),
    ARROW("->", "@|white %s|@"),
    REPLACE("±", "@|magenta %s|@"),
    EXISTING("=", "@|cyan %s|@");
    private final String symbol;
    private final String color;

    ChangeColor(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }

    public String toColor() {
        return color.formatted(symbol);
    }

    public String color(String string) {
        return color.formatted(string);
    }

    public String color(Object... args) {
        return color.formatted(args);
    }
}
