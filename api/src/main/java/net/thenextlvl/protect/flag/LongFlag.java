package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;

public interface LongFlag extends Flag {
    @Contract(pure = true)
    long getDefaultValue();

    @Contract(pure = true)
    long getValue();

    @Contract(value = "_ -> new", pure = true)
    LongFlag withValue(long value);

    @Contract(value = "_, _ -> new", pure = true)
    static LongFlag create(final Key key, final long defaultValue) {
        return new SimpleLongFlag(key, defaultValue);
    }
}
