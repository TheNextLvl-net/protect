package net.thenextlvl.protect.flag;

import lombok.Getter;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<Flag<?>>> registry = new HashMap<>();

    @Override
    public @NotNull Set<Flag<?>> getFlags() {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<Flag<?>> getFlags(@NotNull Plugin plugin) {
        return registry.get(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @NotNull Optional<Flag<T>> getFlag(@NotNull NamespacedKey key) {
        return getFlags().stream()
                .filter(flag -> flag.key().equals(key))
                .map(flag -> (Flag<T>) flag)
                .findAny();
    }

    @Override
    public @NotNull <T> Flag<T> register(@NotNull Plugin plugin, @NotNull Class<? extends T> type, @KeyPattern.Value @NotNull String name, T defaultValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftFlag<>(key, type, defaultValue));
    }

    @Override
    public @NotNull <T> ProtectionFlag<T> register(@NotNull Plugin plugin, @NotNull Class<? extends T> type, @KeyPattern.Value @NotNull String name, T defaultValue, T protectedValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftProtectionFlag<>(key, type, defaultValue, protectedValue));
    }

    private <T extends Flag<?>> T register(@NotNull Plugin plugin, @KeyPattern.Value @NotNull String name, Function<NamespacedKey, T> function) {
        var key = new NamespacedKey(plugin, name);
        var flag = function.apply(key);
        if (registry.computeIfAbsent(plugin, p -> new HashSet<>()).add(flag)) return flag;
        throw new IllegalStateException("Already registered flag: " + key);
    }

    @Override
    public boolean unregister(@NotNull NamespacedKey key) {
        for (var flags : registry.values()) if (flags.removeIf(flag -> flag.key().equals(key))) return true;
        return false;
    }

    @Override
    public boolean unregisterAll(@NotNull Plugin plugin) {
        var remove = registry.remove(plugin);
        return remove != null && !remove.isEmpty();
    }
}
