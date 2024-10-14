package net.thenextlvl.protect.flag;

/**
 * Represents a protection flag with a specific value type.
 *
 * @param <T> The type of the flag value.
 */
public interface ProtectionFlag<T> extends Flag<T> {
    /**
     * Retrieves the protected value associated with this flag.
     *
     * @return the protected value of the flag
     */
    T protectedValue();
}
