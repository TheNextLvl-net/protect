package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a flag with a specific value type.
 *
 * @param <T> The type of the flag value.
 */
public interface Flag<T> extends Comparable<Flag<?>> {
    /**
     * Retrieves the {@link NamespacedKey} associated with this flag.
     *
     * @return the NamespacedKey of the flag
     */
    @NotNull NamespacedKey key();

    /**
     * Retrieves the type of the flag value.
     *
     * @return the type of the flag value
     */
    @NotNull Class<? extends T> type();

    /**
     * Retrieves the default value for a flag.
     *
     * @return The default value for the flag.
     */
    T defaultValue();
}
