package net.thenextlvl.protect.flag;

import com.google.common.base.Preconditions;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
@MethodsReturnNotNullByDefault
public class CraftFlagRegistry implements FlagRegistry {
    private final Map<NamespacedKey, Flag<?>> registry = new HashMap<>();

    @Override
    public @NotNull Set<Flag<?>> getFlags(@NotNull Plugin plugin) {
        return registry.values().stream()
                .filter(flag -> flag.key().getKey().equals(plugin.getName().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Flag<T>> getFlag(NamespacedKey key) {
        return Optional.ofNullable((Flag<T>) registry.get(key));
    }

    @Override
    public @NotNull <T> Flag<T> register(@NotNull Plugin plugin, @NotNull String name, T defaultValue) throws IllegalStateException {
        var key = new NamespacedKey(plugin, name);
        Preconditions.checkState(!registry.containsKey(key), "already registered flag: " + key);
        var flag = new CraftFlag<>(key, defaultValue);
        registry.put(key, flag);
        return flag;
    }

    @Override
    public void unregister(NamespacedKey flag) throws IllegalStateException {
        Preconditions.checkState(registry.containsKey(flag), "never registered flag: " + flag);
        registry.remove(flag);
    }

    @Override
    public void unregisterAll(@NotNull Plugin plugin) {
        registry.keySet().removeIf(key -> key.getKey().equals(plugin.getName().toLowerCase(Locale.ROOT)));
    }
}
