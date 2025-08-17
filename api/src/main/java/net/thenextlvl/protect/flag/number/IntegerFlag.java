package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class IntegerFlag extends NumberFlag<Integer> {
    public IntegerFlag(@NonNull Key key, @Nullable Integer defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public IntegerFlag(@NonNull Key key, @NonNull Integer defaultValue) {
        super(key, defaultValue);
    }

    public IntegerFlag(@NonNull Key key) {
        super(key);
    }
}
