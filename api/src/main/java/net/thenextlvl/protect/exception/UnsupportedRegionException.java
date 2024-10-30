package net.thenextlvl.protect.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class UnsupportedRegionException extends IllegalArgumentException {
    private final @NotNull Class<?> type;

    public UnsupportedRegionException(@NotNull Class<?> type) {
        this.type = type;
    }

    public UnsupportedRegionException(@NotNull Class<?> type, @NotNull String message) {
        super(message);
        this.type = type;
    }

    public UnsupportedRegionException(@NotNull Class<?> type, @NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public UnsupportedRegionException(@NotNull Class<?> type, @NotNull Throwable cause) {
        super(cause);
        this.type = type;
    }
}
