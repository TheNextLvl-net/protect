package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Represents a flag with a specific value type.
 *
 * @param <T> The type of the flag value.
 */
public interface Flag<T> extends Comparable<@NonNull Flag<?>> {
    /**
     * Retrieves the {@link Key} associated with this flag.
     *
     * @return the Key of the flag
     */
    @NonNull
    @Contract(pure = true)
    Key key();

    /**
     * Retrieves the type of the flag value.
     *
     * @return the type of the flag value
     */
    @NonNull
    @Contract(pure = true)
    Class<? extends T> type();

    /**
     * Retrieves the default value for a flag.
     *
     * @return The default value for the flag.
     */
    @Contract(pure = true)
    T defaultValue();
}
