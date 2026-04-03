package net.thenextlvl.protect.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class CircularInheritanceException extends IllegalStateException {
    public CircularInheritanceException(final Throwable cause) {
        super(cause);
    }

    public CircularInheritanceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CircularInheritanceException(final String message) {
        super(message);
    }

    public CircularInheritanceException() {
    }
}
