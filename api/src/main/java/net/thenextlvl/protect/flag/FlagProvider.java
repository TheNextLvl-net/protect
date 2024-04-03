package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The FlagProvider interface represents an object that can store and retrieve flags and their associated states.
 */
public interface FlagProvider {

    /**
     * Retrieves the map of all flags and their associated state.
     *
     * @return a map of flags and their associated state
     */
    @NotNull Map<Flag<?>, @Nullable Object> getFlags();

    /**
     * Sets the map of all flags and their associated state.
     *
     * @param flags A map of flags and their associated states.
     */
    void setFlags(@NotNull Map<Flag<?>, @Nullable Object> flags);

    /**
     * Sets the state of a flag.
     *
     * @param flag  The flag to set the state of.
     * @param state The state to be set for the flag.
     * @param <T>   The type of the flag state.
     */
    default <T> void setFlag(@NotNull Flag<T> flag, T state) {
        getFlags().put(flag, state);
    }

    /**
     * Retrieves the state of a flag.
     *
     * @param flag The flag to retrieve the state of.
     * @param <T>  The type of the flag state.
     * @return The state of the flag.
     */
    @SuppressWarnings("unchecked")
    default <T> T getFlag(@NotNull Flag<T> flag) {
        return (T) getFlags().getOrDefault(flag, flag.defaultValue());
    }

    /**
     * Checks if the flag has a state defined.
     *
     * @param flag The flag to check the state of.
     * @param <T>  The type of the flag.
     * @return true if a state is defined, true otherwise.
     */
    default <T> boolean hasFlag(@NotNull Flag<T> flag) {
        return getFlags().containsKey(flag);
    }

    /**
     * Removes a flag from the FlagProvider.
     *
     * @param flag The flag to remove.
     * @param <T>  The type of the flag value.
     * @return true if the flag was successfully removed, false otherwise.
     */
    default <T> boolean removeFlag(@NotNull Flag<T> flag) {
        return getFlags().remove(flag) != null;
    }
}
