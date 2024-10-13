package net.thenextlvl.protect.area;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaCreateEvent;
import net.thenextlvl.protect.event.AreaDeleteEvent;
import net.thenextlvl.protect.event.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.event.PlayerAreaTransitionEvent;
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
    public CuboidArea create(@NamePattern.Regionized String name, World world, BlockVector3 pos1, BlockVector3 pos2) {
        return create(name, world, new CuboidRegion(pos1, pos2));
    }

    @Override
    public CuboidArea create(@NamePattern.Regionized String name, World world, CuboidRegion region) {
        return create(new CraftCuboidArea(plugin.schematicFolder(), name, world, region.clone(), 0));
    }

    @Override
    public CylinderArea create(@NamePattern.Regionized String name, World world, CylinderRegion region) {
        return create(new CraftCylinderArea(plugin.schematicFolder(), name, world, region.clone(), 0));
    }

    @Override
    public EllipsoidArea create(@NamePattern.Regionized String name, World world, EllipsoidRegion region) {
        return create(new CraftEllipsoidArea(plugin.schematicFolder(), name, world, region.clone(), 0));
    }

    @Override
    public IntersectionArea create(@NamePattern.Regionized String name, World world, RegionIntersection region) {
        var intersection = new RegionIntersection(region.getWorld(), region.getRegions());
        return create(new CraftIntersectionArea(plugin.schematicFolder(), name, world, intersection, 0));
    }

    private <A extends Area> A create(A area) {
        plugin.areaProvider().loadArea(area);
        new AreaCreateEvent<>(area).callEvent();
        return area;
    }

    @Override
    public <T extends Region> boolean delete(RegionizedArea<T> area) {
        if (!new AreaDeleteEvent<>(area).callEvent()) return false;
        var remove = plugin.areaProvider().deleteArea(area);
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
