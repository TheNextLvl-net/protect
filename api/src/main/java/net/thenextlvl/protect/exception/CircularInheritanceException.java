package net.thenextlvl.protect.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class CircularInheritanceException extends IllegalStateException {
    public CircularInheritanceException(Throwable cause) {
        super(cause);
    }

    public CircularInheritanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CircularInheritanceException(String message) {
        super(message);
    }

    public CircularInheritanceException() {
    }
}
