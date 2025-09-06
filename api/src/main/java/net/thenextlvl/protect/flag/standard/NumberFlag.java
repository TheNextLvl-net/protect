package net.thenextlvl.protect.flag.standard;

import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class NumberFlag<T extends Number> extends Flag<T> {
    protected NumberFlag(@NonNull Key key, @Nullable T defaultValue, @Nullable T protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    protected NumberFlag(@NonNull Key key, @NonNull T defaultValue, @Nullable T protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    protected NumberFlag(@NonNull Key key, @Nullable T defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    protected NumberFlag(@NonNull Key key) {
        super(key);
    }
}
