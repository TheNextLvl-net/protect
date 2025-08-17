package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class DoubleFlag extends NumberFlag<Double> {
    public DoubleFlag(@NonNull Key key, @Nullable Double defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public DoubleFlag(@NonNull Key key, @NonNull Double defaultValue) {
        super(key, defaultValue);
    }

    public DoubleFlag(@NonNull Key key) {
        super(key);
    }
}
