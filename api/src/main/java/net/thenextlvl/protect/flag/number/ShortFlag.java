package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ShortFlag extends NumberFlag<Short> {
    public ShortFlag(@NonNull Key key, @Nullable Short defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public ShortFlag(@NonNull Key key, @NonNull Short defaultValue) {
        super(key, defaultValue);
    }

    public ShortFlag(@NonNull Key key) {
        super(key);
    }
}
