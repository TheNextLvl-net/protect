package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * The FlagProvider interface represents an object that can store and retrieve flags and their associated states.
 */
public interface FlagProvider {
    /**
     * Retrieves all locally defined flags.
     *
     * @return an unmodifiable set containing locally defined flags.
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<Flag> getFlags();

    /**
     * Sets the flags for the FlagProvider instance.
     * This method updates only the specified flags in the provided set.
     * Any flags that were already defined but not included in the new set will remain unchanged.
     *
     * @param flags A set of value-bearing flags.
     */
    @Contract(mutates = "this")
    void setFlags(Set<Flag> flags);

    /**
     * Sets the state carried by a value-bearing flag.
     *
     * @param flag the value-bearing flag to set
     * @return whether the flag was changed
     */
    @Contract(mutates = "this")
    boolean setFlag(Flag flag);

    /**
     * Checks if the flag has a state defined.
     *
     * @param flag The flag to check the state of.
     * @return true if a state is defined, true otherwise.
     */
    @Contract(pure = true)
    boolean hasFlag(Flag flag);

    /**
     * Removes a flag from the FlagProvider.
     *
     * @param flag The flag to remove.
     * @return true if the flag was successfully removed, false otherwise.
     */
    @Contract(mutates = "this")
    boolean removeFlag(Flag flag);

    @Contract(pure = true)
    <T extends Flag> T getFlag(FlagHolder<T> flagHolder);
}
