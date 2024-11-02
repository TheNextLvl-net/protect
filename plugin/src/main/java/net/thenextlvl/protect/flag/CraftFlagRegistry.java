package net.thenextlvl.protect.flag;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<Flag<?>>> registry = new HashMap<>();

    @Override
    @NullMarked
    public Set<Flag<?>> getFlags() {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    @NullMarked
    public Set<Flag<?>> getFlags(Plugin plugin) {
        return registry.get(plugin);
    }

    @Override
    @NullMarked
    @SuppressWarnings("unchecked")
    public <T> Optional<Flag<T>> getFlag(Key key) {
        return getFlags().stream()
                .filter(flag -> flag.key().equals(key))
                .map(flag -> (Flag<T>) flag)
                .findAny();
    }

    @Override
    public @NonNull <T> Flag<T> register(@NonNull Plugin plugin, @NonNull Class<? extends T> type, @KeyPattern.Value @NonNull String name, T defaultValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftFlag<>(key, type, defaultValue));
    }

    @Override
    public @NonNull <T> ProtectionFlag<T> register(@NonNull Plugin plugin, @NonNull Class<? extends T> type, @KeyPattern.Value @NonNull String name, T defaultValue, T protectedValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftProtectionFlag<>(key, type, defaultValue, protectedValue));
    }

    @SuppressWarnings("PatternValidation")
    private <T extends Flag<?>> T register(@NonNull Plugin plugin, @KeyPattern.Value @NonNull String name, @NonNull Function<Key, T> function) {
        var key = Key.key(plugin.getName().replace("-", "_").toLowerCase(), name);
        var flag = function.apply(key);
        if (registry.computeIfAbsent(plugin, p -> new HashSet<>()).add(flag)) return flag;
        throw new IllegalStateException("Already registered flag: " + key);
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
