package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class LongFlag extends NumberFlag<Long> {
    public LongFlag(@NonNull Key key, @Nullable Long defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public LongFlag(@NonNull Key key, @NonNull Long defaultValue) {
        super(key, defaultValue);
    }

    public LongFlag(@NonNull Key key) {
        super(key);
    }
}
