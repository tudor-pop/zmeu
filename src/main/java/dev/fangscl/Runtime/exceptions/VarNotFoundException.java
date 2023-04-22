package dev.fangscl.Runtime.exceptions;

public class VarNotFoundException extends RuntimeException {
    private static final String msg = "Variable not found: %s";

    public VarNotFoundException(String message) {
        super(msg.formatted(message));
    }
}
