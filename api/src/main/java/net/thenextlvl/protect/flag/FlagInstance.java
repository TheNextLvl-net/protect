package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * Represents a flag, bound to the {@link FlagType} it holds.
 *
 * @param <T> the type of the flag value
 * @since 4.0.0
 */
public sealed interface FlagInstance<T> extends Keyed permits ProtectionFlagInstance, SimpleFlagInstance {
    /**
     * Retrieves the type of this flag.
     *
     * @return the type of the flag
     * @since 4.0.0
     */
    @Contract(pure = true)
    FlagType<T> flagType();

    /**
     * Retrieves the default value of the flag.
     *
     * @return the default value of the flag
     * @since 4.0.0
     */
    @Contract(pure = true)
    T defaultValue();

    /**
     * Creates a new flag holding the given value for this instance.
     *
     * @param value the flag value
     * @return a new flag holding the value
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    default Flag<T> withValue(final T value) {
        return new SimpleFlag<>(this, value);
    }

    /**
     * Creates a new flag instance without a default value.
     *
     * @param key      the key of the flag
     * @param flagType the type of the flag
     * @param <T>      the type of the flag value
     * @return the new flag instance
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <T> FlagInstance<T> create(final Key key, final FlagType<T> flagType) {
        return create(key, flagType, null);
    }

    /**
     * Creates a new flag instance.
     *
     * @param key          the key of the flag
     * @param flagType     the type of the flag
     * @param defaultValue the default value of the flag, or {@code null} for none
     * @param <T>          the type of the flag value
     * @return the new flag instance
     * @since 4.0.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static <T> FlagInstance<T> create(final Key key, final FlagType<T> flagType, @Nullable final T defaultValue) {
        return new SimpleFlagInstance<>(key, flagType, defaultValue);
    }
}
