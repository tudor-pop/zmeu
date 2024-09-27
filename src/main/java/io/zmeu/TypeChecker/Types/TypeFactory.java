package io.zmeu.TypeChecker.Types;

public class TypeFactory {
    private final static FunStore funStore = FunStore.getInstance();

    public static Type fromString(String symbol) {
        if (symbol.startsWith("(")) {
            FunType fun = funStore.getFun(symbol);
            if (fun != null) {
                return fun;
            } else {
                funStore.setFun(symbol, FunType.valueOf(symbol));
                return funStore.getFun(symbol);
            }
        } else {
            return ValueType.of(symbol);
        }
//        throw new IllegalArgumentException("Invalid symbol: " + symbol);
    }
}
