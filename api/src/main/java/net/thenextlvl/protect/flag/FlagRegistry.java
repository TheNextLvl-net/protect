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
    @NotNull Set<@NotNull Flag<?>> getFlags();

    /**
     * Retrieves the set of flags associated with the given plugin.
     *
     * @param plugin the plugin for which to retrieve the flags
     * @return a set of flags associated with the plugin
     */
    @NotNull Set<@NotNull Flag<?>> getFlags(@NotNull Plugin plugin);

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
     * @param <T>          the type of the flag value
     * @param plugin       the plugin registering the flag
     * @param name         the name of the flag
     * @param defaultValue the default value of the flag
     * @return the registered flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     * @see #register(Plugin, Class, String, Object)
     */
    @SuppressWarnings("unchecked")
    default <T> @NotNull Flag<@NotNull T> register(@NotNull Plugin plugin, @KeyPattern.Value String name,
                                                   @NotNull T defaultValue
    ) throws IllegalStateException {
        return register(plugin, (Class<T>) defaultValue.getClass(), name, defaultValue);
    }

    /**
     * Registers a new flag with the specified plugin, type, name, and default value.
     *
     * @param <T>          the type of the flag value
     * @param plugin       the plugin registering the flag
     * @param type         the class type of the flag value
     * @param name         the name of the flag
     * @param defaultValue the default value of the flag
     * @return the registered flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     */
    <T> @NotNull Flag<T> register(@NotNull Plugin plugin, @NotNull Class<? extends T> type,
                                  @KeyPattern.Value String name, T defaultValue
    ) throws IllegalStateException;

    /**
     * Registers a new protection flag with the specified plugin, name, default value, and protected value.
     *
     * @param <T>            the type of the flag value
     * @param plugin         the plugin registering the flag
     * @param name           the name of the flag
     * @param defaultValue   the default value of the flag
     * @param protectedValue the protected value of the flag, which is typically opposite to the default value
     * @return the registered protection flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     * @see #register(Plugin, Class, String, Object, Object)
     */
    @SuppressWarnings("unchecked")
    default <T> @NotNull ProtectionFlag<@NotNull T> register(@NotNull Plugin plugin, @KeyPattern.Value @NotNull String name,
                                                             @NotNull T defaultValue, @NotNull T protectedValue
    ) throws IllegalStateException {
        return register(plugin, (Class<T>) defaultValue.getClass(), name, defaultValue, protectedValue);
    }

    /**
     * Registers a new protection flag with the specified plugin, name, default value, and protected value.
     * <p>
     * protectedValue defines (generally the opposite of defaultValue) what the flag value is to protect against it,
     * for example, taking the flag 'explosions', protectedValue would be false and defaultValue true
     * <p>
     * {@code var explosions = register(plugin, Boolean.class, "explosions", true, false);}
     *
     * @param <T>            the type of the flag value
     * @param plugin         the plugin registering the flag
     * @param type           the class type of the flag value
     * @param name           the name of the flag
     * @param defaultValue   the default value of the flag
     * @param protectedValue the protected value of the flag, which is typically opposite to the default value
     * @return the registered protection flag
     * @throws IllegalStateException if a flag by the same plugin with the same name is already registered
     */
    <T> @NotNull ProtectionFlag<T> register(@NotNull Plugin plugin, @NotNull Class<? extends T> type,
                                            @KeyPattern.Value @NotNull String name, T defaultValue, T protectedValue
    ) throws IllegalStateException;

    /**
     * Unregisters a flag identified by the given NamespacedKey.
     *
     * @param flag the NamespacedKey of the flag to unregister
     * @return true if the flag was unregistered, false otherwise
     */
    boolean unregister(@NotNull NamespacedKey flag);

    /**
     * Unregisters all flags associated with the specified plugin.
     *
     * @param plugin the plugin for which to unregister flags
     * @return true if any flag was unregistered, false otherwise
     */
    boolean unregisterAll(@NotNull Plugin plugin);
}
