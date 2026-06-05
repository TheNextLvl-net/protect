package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * FlagRegistry is an interface that provides methods for managing registered flags.
 */
public interface FlagRegistry {
    /**
     * Retrieves the set of flags associated with this FlagRegistry.
     *
     * @return a Set of flags associated with the FlagRegistry
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<Flag> getFlags();

    /**
     * Retrieves the set of flags associated with the given plugin.
     *
     * @param plugin the plugin for which to retrieve the flags
     * @return a set of flags associated with the plugin
     */
    @Contract(pure = true)
    Set<Flag> getFlags(Plugin plugin);

    /**
     * Retrieves the flag associated with the given Key.
     *
     * @param key the Key of the flag to retrieve
     * @return an Optional containing the flag, or an empty Optional if no flag was found
     */
    @Contract(pure = true)
    Optional<Flag> getFlag(Key key);

    /**
     * Registers a new flag with the specified plugin.
     *
     * @param plugin the plugin registering the flag
     * @param flag   the flag to register
     * @return the registered flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     */
    @Contract(mutates = "this")
    <T extends Flag> FlagHolder<T> register(Plugin plugin, T flag) throws IllegalStateException;

    /**
     * Unregisters a flag identified by the given Key.
     *
     * @param flag the Key of the flag to unregister
     * @return true if the flag was unregistered, false otherwise
     */
    @Contract(mutates = "this")
    boolean unregister(Key flag);

    /**
     * Unregisters all flags associated with the specified plugin.
     *
     * @param plugin the plugin for which to unregister flags
     * @return true if any flag was unregistered, false otherwise
     */
    @Contract(mutates = "this")
    boolean unregisterAll(Plugin plugin);
}
