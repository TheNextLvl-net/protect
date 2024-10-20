package net.thenextlvl.protect.area;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaDeleteEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaTransitionEvent;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftAreaService implements AreaService {
    public final Map<Class<? extends Area>, AreaAdapter<?>> areaAdapters = new HashMap<>();
    private final ProtectPlugin plugin;

    @Override
    public <T extends Region> AreaCreator<T> creator(@NamePattern.Regionized String name, World world, T region) {
        return new CraftAreaCreator<>(plugin, name, world, region);
    }

    @Override
    public <T extends Region> boolean delete(RegionizedArea<T> area) {
        if (!new AreaDeleteEvent(area).callEvent()) return false;
        var remove = plugin.areaProvider().delete(area);
        if (remove) handlePostRemove(area);
        return remove;
    }

    @Override
    public Map<Class<? extends Area>, AreaAdapter<?>> getAdapters() {
        return areaAdapters;
    }

    @Override
    public <T extends Area> void registerAdapter(Class<T> type, AreaAdapter<T> adapter) throws IllegalStateException {
        Preconditions.checkState(!getAdapters().containsKey(type), "Duplicate adapter for type: " + type.getName());
        Preconditions.checkState(getAdapters().values().stream()
                .filter(value -> value.getNamespacedKey().equals(adapter.getNamespacedKey()))
                .findAny().isEmpty(), "Duplicate key for adapter: " + adapter.getNamespacedKey() + " in "
                                      + adapter.getClass().getName());
        getAdapters().put(type, adapter);
    }

    @Override
    public <T extends Area> boolean unregisterAdapter(Class<T> type) {
        return getAdapters().remove(type) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Area> AreaAdapter<T> getAdapter(Class<T> type) throws NullPointerException {
        return (AreaAdapter<T>) Objects.requireNonNull(getAdapters().get(type), "No adapter for type: " + type.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Area> AreaAdapter<T> getAdapter(NamespacedKey key) throws IllegalArgumentException {
        return (AreaAdapter<T>) getAdapters().values().stream()
                .filter(adapter -> adapter.getNamespacedKey().equals(key))
                .findAny().orElseThrow(() -> new IllegalArgumentException("No adapter for key: " + key));
    }

    private void handlePostRemove(Area removed) {
        removed.getPlayers().forEach(player -> {
            new PlayerAreaLeaveEvent(player, removed).callEvent();
            var target = plugin.areaProvider().getArea(player);
            if (removed.getPriority() < target.getPriority()) return;
            new PlayerAreaTransitionEvent(player, removed, target).callEvent();
        });
    }
}
