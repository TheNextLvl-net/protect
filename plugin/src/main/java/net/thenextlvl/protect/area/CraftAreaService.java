package net.thenextlvl.protect.area;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.regions.Region;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaDeleteEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaTransitionEvent;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CraftAreaService implements AreaService {
    private final @NotNull Map<@NotNull Class<? extends Area>, @NotNull AreaAdapter<?>> adapters = new HashMap<>();
    private final @NotNull Set<@NotNull RegionWrapper<?>> wrappers = new HashSet<>();
    private final @NotNull ProtectPlugin plugin;

    private record RegionWrapper<T extends Region>(
            @NotNull Class<T> type,
            @NotNull Function<AreaCreator<T>, RegionizedArea<T>> creator
    ) {
    }

    @Override
    public <T extends Region> @NotNull AreaCreator<T> creator(@NotNull String name, @NotNull World world, @NotNull T region) {
        return new CraftAreaCreator<>(plugin, name, world, region);
    }

    @Override
    public <T extends Region> boolean delete(@NotNull RegionizedArea<@NotNull T> area) {
        if (!new AreaDeleteEvent(area).callEvent()) return false;
        var remove = plugin.areaProvider().delete(area);
        if (remove) handlePostRemove(area);
        return remove;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Region> @NotNull Optional<Function<AreaCreator<T>, RegionizedArea<T>>> getWrapper(@NotNull Class<T> type) {
        return wrappers.stream()
                .filter(wrapper -> wrapper.type().isAssignableFrom(type))
                .map(wrapper -> (RegionWrapper<T>) wrapper)
                .map(RegionWrapper::creator)
                .findFirst();
    }

    @Override
    public <T extends Region> boolean unregisterWrapper(@NotNull Class<T> type) {
        return wrappers.removeIf(wrapper -> wrapper.type().isAssignableFrom(type));
    }

    @Override
    public <T extends Region> void registerWrapper(@NotNull Class<T> type, @NotNull Function<AreaCreator<T>, RegionizedArea<T>> creator) throws IllegalStateException {
        var wrapper = new RegionWrapper<>(type, creator);
        Preconditions.checkState(wrappers.add(wrapper), "Duplicate wrapper for type: " + type.getName());
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull Class<? extends Region>> getRegionWrappers() {
        return wrappers.stream().map(RegionWrapper::type)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable @NotNull Map<@NotNull Class<? extends Area>, AreaAdapter<?>> getAdapters() {
        return Map.copyOf(adapters);
    }

    @Override
    public <T extends Area> void registerAdapter(@NotNull Class<T> type, @NotNull AreaAdapter<T> adapter) throws IllegalStateException {
        Preconditions.checkState(!adapters.containsKey(type), "Duplicate adapter for type: " + type.getName());
        Preconditions.checkState(adapters.values().stream()
                .filter(value -> value.key().equals(adapter.key()))
                .findAny().isEmpty(), "Duplicate key for adapter: " + adapter.key() + " in "
                                      + adapter.getClass().getName());
        adapters.put(type, adapter);
    }

    @Override
    public <T extends Area> boolean unregisterAdapter(@NotNull Class<T> type) {
        return adapters.remove(type) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Area> @NotNull AreaAdapter<T> getAdapter(@NotNull Class<T> type) throws NullPointerException {
        return (AreaAdapter<T>) Objects.requireNonNull(adapters.get(type), "No adapter for type: " + type.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Area> @NotNull AreaAdapter<T> getAdapter(@NotNull NamespacedKey key) throws IllegalArgumentException {
        return (AreaAdapter<T>) adapters.values().stream()
                .filter(adapter -> adapter.key().equals(key))
                .findAny().orElseThrow(() -> new IllegalArgumentException("No adapter for key: " + key));
    }

    private void handlePostRemove(@NotNull Area removed) {
        removed.getPlayers().forEach(player -> {
            new PlayerAreaLeaveEvent(player, removed).callEvent();
            var target = plugin.areaProvider().getArea(player);
            if (removed.getPriority() < target.getPriority()) return;
            new PlayerAreaTransitionEvent(player, removed, target).callEvent();
        });
    }
}
