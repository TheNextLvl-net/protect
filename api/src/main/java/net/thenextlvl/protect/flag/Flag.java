package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A flag value, bound to the {@link FlagInstance} it belongs to.
 *
 * @param <T> the type of the flag value
 * @see FlagInstance#withValue(Object)
 * @since 4.0.0
 */
public sealed interface Flag<T> permits SimpleFlag {
    /**
     * Retrieves the flag instance this value belongs to.
     *
     * @return the flag instance
     * @since 4.0.0
     */
    @Contract(pure = true)
    FlagInstance<T> instance();

    /**
     * Retrieves the value of this flag.
     *
     * @return the flag value
     * @since 4.0.0
     */
    @Contract(pure = true)
    T value();
}
