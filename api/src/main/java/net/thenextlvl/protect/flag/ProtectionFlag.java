package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;

/**
 * Represents a protection flag.
 */
public interface ProtectionFlag extends BooleanFlag {
    /**
     * Retrieves the protected value associated with this flag.
     *
     * @return the protected value of the flag
     */
    @Contract(pure = true)
    boolean getProtectedValue();

    @Override
    @Contract(value = "_ -> new", pure = true)
    ProtectionFlag withValue(boolean value);

    @Contract(value = "_, _, _ -> new", pure = true)
    static ProtectionFlag create(final Key key, final boolean defaultValue, final boolean protectedValue) {
        return new SimpleProtectionFlag(key, defaultValue, protectedValue);
    }
}
