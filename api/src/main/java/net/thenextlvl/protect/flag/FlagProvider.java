package net.thenextlvl.protect.flag;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * The FlagProvider interface represents an object that can store and retrieve flags and their associated states.
 */
public interface FlagProvider {
    /**
     * Retrieves a map of all the flags and their associated states.
     *
     * @return an unmodifiable map containing the flags as keys and their states as values.
     * The map may include null values for certain flags.
     */
    @NonNull Map<@NonNull Flag<?>, @Nullable Object> getFlags();

    /**
     * Sets the flags for the FlagProvider instance.
     * This method updates only the specified flags in the provided map.
     * Any flags that were already defined but not included in the new map will remain unchanged.
     *
     * @param flags A map of flags and their associated states.
     */
    void setFlags(@NonNull Map<@NonNull Flag<?>, @Nullable Object> flags);

    /**
     * Sets the state of a flag.
     *
     * @param flag  The flag to set the state of.
     * @param state The state to be set for the flag.
     * @param <T>   The type of the flag state.
     * @return whether the flag was changed.
     */
    <T> boolean setFlag(@NonNull Flag<T> flag, T state);

    /**
     * Retrieves the value of the specified flag for this area. This method always returns a value.
     * If the flag is not defined in this area, a value inherited from a parent area will be returned.
     * If there is no inherited mapping, the default value of the flag will be returned.
     * To check the presence of a flag, use {@link #hasFlag(Flag)}.
     *
     * @param flag The flag to retrieve the value for.
     * @param <T>  The type of the flag value.
     * @return The value of the specified flag. Nullability is defined by the flag's type.
     */
    <T> T getFlag(@NonNull Flag<T> flag);

    /**
     * Checks if the flag has a state defined.
     *
     * @param flag The flag to check the state of.
     * @param <T>  The type of the flag.
     * @return true if a state is defined, true otherwise.
     */
    <T> boolean hasFlag(@NonNull Flag<T> flag);

    /**
     * Removes a flag from the FlagProvider.
     *
     * @param flag The flag to remove.
     * @param <T>  The type of the flag value.
     * @return true if the flag was successfully removed, false otherwise.
     */
    <T> boolean removeFlag(@NonNull Flag<T> flag);
}
