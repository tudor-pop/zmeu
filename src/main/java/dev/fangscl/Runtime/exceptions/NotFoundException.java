package dev.fangscl.Runtime.exceptions;

import lombok.Getter;

public class NotFoundException extends RuntimeException {
    @Getter
    private Object objectNotFound;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object objectNotFound) {
        super(message + objectNotFound);
        this.objectNotFound = objectNotFound;
    }
}
