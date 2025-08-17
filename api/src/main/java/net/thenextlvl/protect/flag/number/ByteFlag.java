package net.thenextlvl.protect.flag.number;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ByteFlag extends NumberFlag<Byte> {
    public ByteFlag(@NonNull Key key, @Nullable Byte defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public ByteFlag(@NonNull Key key, @NonNull Byte defaultValue) {
        super(key, defaultValue);
    }

    public ByteFlag(@NonNull Key key) {
        super(key);
    }
}
