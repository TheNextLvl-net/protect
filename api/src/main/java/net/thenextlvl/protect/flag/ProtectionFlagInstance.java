package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;

/**
 * A protection flag, which additionally provides the value that protects an area.
 *
 * @param <T> the type of the flag value
 * @since 4.0.0
 */
public sealed interface ProtectionFlagInstance<T> extends FlagInstance<T> permits SimpleProtectionFlagInstance {
    /**
     * Retrieves the protected value of the flag, which is typically opposite to the default value.
     *
     * @return the protected value of the flag
     * @since 4.0.0
     */
    @Contract(pure = true)
    T protectedValue();

    /**
     * Creates a new protection flag.
     * <p>
     * The protected value (generally the opposite of the default value) is the value the flag must hold
     * to protect against it. For example, for an {@code explosions} flag, the protected value would be
     * {@code false} and the default value {@code true}.
     *
     * @param key            the key of the flag
     * @param flagType       the type of the flag
     * @param defaultValue   the default value of the flag
     * @param protectedValue the protected value of the flag, which is typically opposite to the default value
     * @param <T>            the type of the flag value
     * @return the new protection flag
     * @since 4.0.0
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    static <T> ProtectionFlagInstance<T> create(final Key key, final FlagType<T> flagType, final T defaultValue, final T protectedValue) {
        return new SimpleProtectionFlagInstance<>(key, flagType, defaultValue, protectedValue);
    }
}
