package dev.fangscl.Diff;

import lombok.Getter;

@Getter
public enum Change {
    COLORED_OPERATION("~", "@|yellow %s|@"), REMOVE("-", "@|red %s|@"), ADD("+", "@|green %s|@");
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
