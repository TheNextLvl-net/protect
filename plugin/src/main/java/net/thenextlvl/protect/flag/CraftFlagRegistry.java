package net.thenextlvl.protect.flag;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class CraftFlagRegistry implements FlagRegistry {
    private final Map<Plugin, Set<FlagInstance<?>>> registry = new ConcurrentHashMap<>();
    private final Map<Key, FlagInstance<?>> flags = new ConcurrentHashMap<>();
    private volatile boolean frozen = false;

    public Map<Plugin, Set<FlagInstance<?>>> getRegistry() {
        return registry;
    }

    @Override
    public Set<FlagInstance<?>> getFlags() {
        return Set.copyOf(flags.values());
    }

    @Override
    public Set<FlagInstance<?>> getFlags(final Plugin plugin) {
        final var flags = registry.get(plugin);
        return flags != null ? Set.copyOf(flags) : Set.of();
    }

    @Override
    public Optional<FlagInstance<?>> getFlag(final Key key) {
        return Optional.ofNullable(flags.get(key));
    }

    public <F extends FlagInstance<?>> F register(final Plugin plugin, final F flag) throws IllegalStateException {
        Preconditions.checkState(!frozen, "Flag registry is frozen, cannot register flag: %s", flag.key().asString());
        if (flags.putIfAbsent(flag.key(), flag) != null)
            throw new IllegalStateException("Already registered flag: " + flag.key().asString());
        registry.computeIfAbsent(plugin, p -> ConcurrentHashMap.newKeySet()).add(flag);
        return flag;
    }

    public void freeze() {
        frozen = true;
    }
}
