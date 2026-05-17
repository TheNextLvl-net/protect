package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<Flag<?>>> registry = new HashMap<>();

    public Map<Plugin, Set<Flag<?>>> getRegistry() {
        return registry;
    }

    @Override
    @NullMarked
    public Set<Flag<?>> getFlags() {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @NullMarked
    public Set<Flag<?>> getFlags(final Plugin plugin) {
        return registry.get(plugin);
    }

    @Override
    @NullMarked
    @SuppressWarnings("unchecked")
    public <T> Optional<Flag<T>> getFlag(final Key key) {
        return getFlags().stream()
                .filter(flag -> flag.key().equals(key))
                .map(flag -> (Flag<T>) flag)
                .findAny();
    }

    @Override
    public @NonNull <T> Flag<T> register(@NonNull final Plugin plugin, @NonNull final Class<? extends T> type, @KeyPattern.Value @NonNull final String name, final T defaultValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftFlag<>(key, type, defaultValue));
    }

    @Override
    public @NonNull <T> ProtectionFlag<T> register(@NonNull final Plugin plugin, @NonNull final Class<? extends T> type, @KeyPattern.Value @NonNull final String name, final T defaultValue, final T protectedValue) throws IllegalStateException {
        return register(plugin, name, key -> new CraftProtectionFlag<>(key, type, defaultValue, protectedValue));
    }

    @SuppressWarnings("PatternValidation")
    private <T extends Flag<?>> T register(@NonNull final Plugin plugin, @KeyPattern.Value @NonNull final String name, @NonNull final Function<Key, T> function) {
        final var key = Key.key(plugin.getName().replace("-", "_").toLowerCase(), name);
        final var flag = function.apply(key);
        if (registry.computeIfAbsent(plugin, p -> new HashSet<>()).add(flag)) return flag;
        throw new IllegalStateException("Already registered flag: " + key);
    }

    @Override
    public boolean unregister(@NonNull final Key key) {
        for (final var flags : registry.values()) if (flags.removeIf(flag -> flag.key().equals(key))) return true;
        return false;
    }

    @Override
    public boolean unregisterAll(@NonNull final Plugin plugin) {
        final var remove = registry.remove(plugin);
        return remove != null && !remove.isEmpty();
    }
}
