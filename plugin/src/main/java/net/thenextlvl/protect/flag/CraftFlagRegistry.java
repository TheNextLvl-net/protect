package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<Flag>> registry = new HashMap<>();

    public Map<Plugin, Set<Flag>> getRegistry() {
        return registry;
    }

    @Override
    public Set<Flag> getFlags() {
        return registry.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Flag> getFlags(final Plugin plugin) {
        return registry.getOrDefault(plugin, Set.of());
    }

    @Override
    public Optional<Flag> getFlag(final Key key) {
        return getFlags().stream()
                .filter(flag -> flag.key().equals(key))
                .findAny();
    }

    @Override
    public <T extends Flag> FlagHolder<T> register(final Plugin plugin, final T flag) throws IllegalStateException {
        if (registry.computeIfAbsent(plugin, p -> new HashSet<>()).add(flag))
            return new SimpleFlagHolder<>(flag);
        throw new IllegalStateException("Already registered flag: " + flag.key());
    }

    @Override
    public boolean unregister(final Key key) {
        for (final var flags : registry.values()) if (flags.removeIf(flag -> flag.key().equals(key))) return true;
        return false;
    }

    @Override
    public boolean unregisterAll(final Plugin plugin) {
        final var remove = registry.remove(plugin);
        return remove != null && !remove.isEmpty();
    }

    private record SimpleFlagHolder<T extends Flag>(
            T flag
    ) implements FlagHolder<T> {
    }
}
