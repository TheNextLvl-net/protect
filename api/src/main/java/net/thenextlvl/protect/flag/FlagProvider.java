package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface FlagProvider {
    /**
     * Sets the state of a flag.
     *
     * @param flag  The flag to set the state of.
     * @param state The state to be set for the flag.
     * @param <T>   The type of the flag state.
     */
    <T> void setFlag(@NotNull Flag<T> flag, T state);

    /**
     * Retrieves the state of a flag.
     *
     * @param flag The flag to retrieve the state of.
     * @param <T>  The type of the flag state.
     * @return The state of the flag.
     */
    <T> T getFlag(@NotNull Flag<T> flag);

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
     * Checks if the flag has a state defined.
     *
     * @param flag The flag to check the state of.
     * @param <T> The type of the flag.
     * @return true if a state is defined, true otherwise.
     */
    <T> boolean hasFlag(@NotNull Flag<T> flag);

    /**
     * Removes a flag from the FlagProvider.
     *
     * @param flag The flag to remove.
     * @param <T>  The type of the flag value.
     * @return true if the flag was successfully removed, false otherwise.
     */
    <T> boolean removeFlag(@NotNull Flag<T> flag);
}
