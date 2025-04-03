package net.thenextlvl.protect.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class UnsupportedRegionException extends IllegalArgumentException {
    private final Class<?> type;

    public UnsupportedRegionException(Class<?> type) {
        this(type, "Unsupported region type: " + type.getName());
    }

    public UnsupportedRegionException(Class<?> type, String message) {
        super(message);
        this.type = type;
    }

    public UnsupportedRegionException(Class<?> type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public UnsupportedRegionException(Class<?> type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
