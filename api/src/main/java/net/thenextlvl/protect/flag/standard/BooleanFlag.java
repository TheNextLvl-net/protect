package net.thenextlvl.protect.flag.standard;

import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;

public final class BooleanFlag extends Flag<Boolean> {
    public BooleanFlag(@NonNull Key key, Boolean defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }
}
