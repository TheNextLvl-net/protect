package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * FlagRegistry is an interface that provides methods for managing registered flags.
 */
public interface FlagRegistry {
    @NonNull
    @Unmodifiable
    @Contract(pure = true)
    Set<@NonNull Flag<?>> getFlags();

    @NonNull
    @Unmodifiable
    @Contract(pure = true)
    Set<@NonNull Flag<?>> getFlags(Plugin plugin);

    /**
     * Retrieves the flag associated with the given key.
     *
     * @param key the key of the flag to retrieve
     * @param <T> the type of the flag
     * @return an {@code Optional} containing the flag, or empty if no flag was found
     */
    @Contract(pure = true)
    <T extends Flag<?>> @NonNull Optional<@NonNull T> getFlag(Key key);

    /**
     * Registers a new flag with the specified plugin.
     *
     * @param plugin the plugin registering the flag
     * @return {@code true} if the flag was registered, {@code false} otherwise
     */
    boolean register(@NonNull Plugin plugin, @NonNull Flag<?> flag);

    default boolean register(@NonNull Flag<?> flag) {
        return register(JavaPlugin.getProvidingPlugin(flag.getClass()), flag);
    }

    /**
     * Unregisters the given flag.
     *
     * @param flag the flag to unregister
     * @return {@code true} if the flag was unregistered, {@code false} otherwise
     */
    default boolean unregister(@NonNull Flag<?> flag) {
        return unregister(flag.key());
    }

    /**
     * Unregisters the flag by the given key.
     *
     * @param key the key of the flag to unregister
     * @return {@code true} if the flag was unregistered, {@code false} otherwise
     */
    boolean unregister(@NonNull Key key);

    /**
     * Unregisters all flags associated with the specified plugin.
     *
     * @param plugin the plugin for which to unregister flags
     * @return {@code true} if any flag was unregistered, {@code false} otherwise
     */
    boolean unregisterAll(@NonNull Plugin plugin);
}
