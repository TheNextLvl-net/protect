package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * FlagRegistry is an interface that provides read access to the registered flags.
 * <p>
 * Flags are registered through {@link net.thenextlvl.protect.lifecycle.LifecycleEvents#FLAG_REGISTRATION}.
 * The registry is frozen afterwards and does not change for the rest of the server's lifetime.
 *
 * @since 4.0.0
 */
public interface FlagRegistry {
    static FlagRegistry instance() {
        final class Holder {
            private static final FlagRegistry INSTANCE = ServiceLoader.load(FlagRegistry.class).findFirst().orElseThrow();
        }
        return Holder.INSTANCE;
    }
    
    /**
     * Retrieves all registered flags.
     *
     * @return an unmodifiable set of all registered flags
     * @since 4.0.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<FlagInstance<?>> getFlags();

    /**
     * Retrieves the flags registered by the given plugin.
     *
     * @param plugin the plugin for which to retrieve the flags
     * @return an unmodifiable set of the flags registered by the plugin
     * @since 4.0.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<FlagInstance<?>> getFlags(Plugin plugin);

    /**
     * Retrieves the flag associated with the given key.
     *
     * @param key the key of the flag to retrieve
     * @return an optional containing the flag, or empty if no flag is registered under the key
     * @since 4.0.0
     */
    @Contract(pure = true)
    Optional<FlagInstance<?>> getFlag(Key key);
}
