package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<Flag<?>>> registry = new HashMap<>();

    public Map<Plugin, Set<Flag<?>>> getRegistry() {
        return registry;
    }

    @SuppressWarnings("PatternValidation")
    private <T extends Flag<?>> T register(@NonNull Plugin plugin, @KeyPattern.Value @NonNull String name, @NonNull Function<Key, T> function) {
        var key = Key.key(plugin.getName().replace("-", "_").toLowerCase(), name);
        var flag = function.apply(key);
        if (registry.computeIfAbsent(plugin, p -> new HashSet<>()).add(flag)) return flag;
        throw new IllegalStateException("Already registered flag: " + key);
    }

    @Override
    public @NonNull @Unmodifiable Set<@NonNull Flag<?>> getFlags() {
        return Set.of();
    }

    @Override
    public @NonNull @Unmodifiable Set<@NonNull Flag<?>> getFlags(Plugin plugin) {
        return Set.of();
    }

    @Override
    public @NonNull <T extends Flag<?>> Optional<@NonNull T> getFlag(Key key) {
        return Optional.empty();
    }

    @Override
    public boolean register(@NonNull Plugin plugin, @NonNull Flag<?> flag) {
        return false;
    }

    @Override
    public boolean unregister(@NonNull Key key) {
        for (var flags : registry.values()) if (flags.removeIf(flag -> flag.key().equals(key))) return true;
        return false;
    }

    @Override
    public boolean unregisterAll(@NonNull Plugin plugin) {
        var remove = registry.remove(plugin);
        return remove != null && !remove.isEmpty();
    }
}
