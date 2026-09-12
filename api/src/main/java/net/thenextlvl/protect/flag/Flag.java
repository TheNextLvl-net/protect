package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;

/**
 * Represents a flag identity.
 */
public interface Flag {
    /**
     * Retrieves the {@link Key} associated with this flag.
     *
     * @return the Key of the flag
     */
    @Contract(pure = true)
    Key key();

    @Contract(pure = true)
    boolean is(Flag flag);
}
