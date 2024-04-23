package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.KeyPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    Set<Flag<?>> getFlags();

    /**
     * Retrieves the set of flags associated with the given plugin.
     *
     * @param plugin the plugin for which to retrieve the flags
     * @return a set of flags associated with the plugin
     */
    @NotNull
    Set<Flag<?>> getFlags(@NotNull Plugin plugin);

    /**
     * Retrieves the flag associated with the given NamespacedKey.
     *
     * @param key the NamespacedKey of the flag to retrieve
     * @param <T> the type of the flag value
     * @return an Optional containing the flag, or an empty Optional if no flag was found
     */
    <T> @NotNull Optional<Flag<T>> getFlag(@NotNull NamespacedKey key);

    /**
     * Registers a new flag with the specified plugin, name, and default value.
     *
     * @param plugin       the plugin registering the flag
     * @param name         the name of the flag
     * @param defaultValue the default value of the flag
     * @param <T>          the type of the flag value
     * @return the registered flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     */
    <T> @NotNull Flag<T> register(@NotNull Plugin plugin, @NotNull Class<? extends T> type, @KeyPattern @NotNull String name, T defaultValue) throws IllegalStateException;

    /**
     * Unregisters a flag identified by the given NamespacedKey.
     *
     * @param flag the NamespacedKey of the flag to unregister
     * @throws IllegalStateException if no flag with the same key was registered
     */
    void unregister(@NotNull NamespacedKey flag) throws IllegalStateException;

    /**
     * Unregisters all flags associated with the specified plugin.
     *
     * @param plugin the plugin for which to unregister flags
     */
    void unregisterAll(@NotNull Plugin plugin);
}
