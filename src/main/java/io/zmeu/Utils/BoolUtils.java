package io.zmeu.Utils;

public class BoolUtils {
    public static boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean b) {
            return b;
        }
        return true;
    }
}
