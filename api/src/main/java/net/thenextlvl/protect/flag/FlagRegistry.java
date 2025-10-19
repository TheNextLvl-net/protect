package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

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
    @NonNull
    @Unmodifiable
    @Contract(pure = true)
    Set<@NonNull Flag<?>> getFlags();

    /**
     * Retrieves the set of flags associated with the given plugin.
     *
     * @param plugin the plugin for which to retrieve the flags
     * @return a set of flags associated with the plugin
     */
    @NonNull
    @Contract(pure = true)
    Set<@NonNull Flag<?>> getFlags(@NonNull Plugin plugin);

    /**
     * Retrieves the flag associated with the given Key.
     *
     * @param key the Key of the flag to retrieve
     * @param <T> the type of the flag value
     * @return an Optional containing the flag, or an empty Optional if no flag was found
     */
    @NonNull
    @Contract(pure = true)
    <T> Optional<@NonNull Flag<T>> getFlag(@NonNull Key key);

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
    @NullMarked
    @SuppressWarnings("unchecked")
    @Contract(mutates = "this")
    default <T> Flag<T> register(Plugin plugin, @KeyPattern.Value String name, T defaultValue) throws IllegalStateException {
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
    @Contract(mutates = "this")
    <T> @NonNull Flag<T> register(@NonNull Plugin plugin, @NonNull Class<? extends T> type,
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
    @NullMarked
    @SuppressWarnings("unchecked")
    @Contract(mutates = "this")
    default <T> ProtectionFlag<T> register(Plugin plugin, @KeyPattern.Value String name, T defaultValue, T protectedValue) throws IllegalStateException {
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
    @Contract(mutates = "this")
    <T> @NonNull ProtectionFlag<T> register(@NonNull Plugin plugin, @NonNull Class<? extends T> type,
                                            @KeyPattern.Value @NonNull String name, T defaultValue, T protectedValue
    ) throws IllegalStateException;

    /**
     * Unregisters a flag identified by the given Key.
     *
     * @param flag the Key of the flag to unregister
     * @return true if the flag was unregistered, false otherwise
     */
    @Contract(mutates = "this")
    boolean unregister(@NonNull Key flag);

    /**
     * Unregisters all flags associated with the specified plugin.
     *
     * @param plugin the plugin for which to unregister flags
     * @return true if any flag was unregistered, false otherwise
     */
    @Contract(mutates = "this")
    boolean unregisterAll(@NonNull Plugin plugin);
}
