package io.zmeu.TypeChecker.Types;

import java.util.HashMap;
import java.util.Map;

public class FunStore {
    private static final Map<String, FunType> store = new HashMap<>();

    public static FunType getFun(String funName) {
        return store.get(funName);
    }

    public static FunType setFun(String funName, FunType funType) {
        return store.put(funName, funType);
    }

    public static FunType setFun(FunType funType) {
        return store.put(funType.name(), funType);
    }

    public static void addFun(String funName, FunType funType) {
        if (getFun(funName) != null) {
            throw new RuntimeException("Fun '" + funName + "' already exists");
        }
        setFun(funName, funType);
    }
}
