package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;

public interface BooleanFlag extends Flag {
    @Contract(pure = true)
    boolean getDefaultValue();

    @Contract(pure = true)
    boolean getValue();

    @Contract(value = "_ -> new", pure = true)
    BooleanFlag withValue(boolean value);

    @Contract(value = "_, _ -> new", pure = true)
    static BooleanFlag create(final Key key, final boolean defaultValue) {
        return new SimpleBooleanFlag(key, defaultValue);
    }
}
