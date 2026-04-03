package net.thenextlvl.protect.exception;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UnsupportedRegionException extends IllegalArgumentException {
    private final Class<?> type;

    public UnsupportedRegionException(final Class<?> type) {
        this(type, "Unsupported region type: " + type.getName());
    }

    public UnsupportedRegionException(final Class<?> type, final String message) {
        super(message);
        this.type = type;
    }

    public UnsupportedRegionException(final Class<?> type, final String message, final Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public UnsupportedRegionException(final Class<?> type, final Throwable cause) {
        super(cause);
        this.type = type;
    }

    @Contract(pure = true)
    public Class<?> getType() {
        return type;
    }
}
