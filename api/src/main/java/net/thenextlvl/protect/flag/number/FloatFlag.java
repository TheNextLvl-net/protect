package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class FloatFlag extends NumberFlag<Float> {
    public FloatFlag(@NonNull Key key, @Nullable Float defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public FloatFlag(@NonNull Key key, @NonNull Float defaultValue) {
        super(key, defaultValue);
    }

    public FloatFlag(@NonNull Key key) {
        super(key);
    }
}
