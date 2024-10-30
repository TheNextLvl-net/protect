package net.thenextlvl.protect.exception;

import org.jetbrains.annotations.NotNull;

public class CircularInheritanceException extends IllegalStateException {
    public CircularInheritanceException(@NotNull Throwable cause) {
        super(cause);
    }

    public CircularInheritanceException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public CircularInheritanceException(@NotNull String message) {
        super(message);
    }

    public CircularInheritanceException() {
    }
}
