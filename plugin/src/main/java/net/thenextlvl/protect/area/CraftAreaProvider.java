package net.thenextlvl.protect.area;

import core.io.IO;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.adapter.area.AreaFile;
import net.thenextlvl.protect.area.event.AreaLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

@NullMarked
@RequiredArgsConstructor
public class CraftAreaProvider implements AreaProvider {
    private final Map<World, Set<Area>> areas = new HashMap<>();
    private final ProtectPlugin plugin;

    @Override
    public Stream<Area> getAreas() {
        return areas.values().stream()
                .flatMap(Collection::stream);
    }

    @Override
    public Stream<Area> getAreas(World world) {
        return areas.getOrDefault(world, Collections.emptySet()).stream();
    }

    @Override
    public Stream<Area> getAreas(Location location) {
        return getAreas(location.getWorld()).filter(area -> area.contains(location));
    }

    @Override
    public Area getArea(Location location) {
        return getAreas(location)
                .max(Comparator.comparingInt(Area::getPriority))
                .orElseGet(() -> getArea(location.getWorld()));
    }

    @Override
    public Optional<Area> getArea(String name) {
        return getAreas().filter(area -> area.getName().equals(name)).findAny();
    }

    @Override
    public GlobalArea getArea(World world) {
        return getAreas(world)
                .filter(area -> area instanceof GlobalArea)
                .map(area -> (GlobalArea) area)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No area found for world " + world.getName()));
    }

    public void load(World world) {
        var areaFolder = new File(world.getWorldFolder(), "areas");
        var files = areaFolder.listFiles((file, name) -> name.endsWith(".dat"));
        if (files != null) for (var file : files) load(world, file);
        if (getAreas(world).anyMatch(area -> area instanceof GlobalArea)) return;
        persist(new CraftGlobalArea(plugin, world));
    }

    public void load(World world, File file) {
        var loaded = read(world, null, IO.of(file));
        new AreaLoadEvent(loaded).callEvent();
        memoize(world, loaded);
    }

    public void persist(Area area) {
        memoize(area.getWorld(), read(area.getWorld(), area, IO.of(area.getFile())));
    }

    private void memoize(World world, Area area) {
        var areas = this.areas.computeIfAbsent(world, k -> new HashSet<>());
        areas.stream().filter(area::equals).findAny()
                .ifPresentOrElse(io -> plugin.getComponentLogger().warn(
                        "Ignoring duplicate area {}: {}",
                        area.getName(), area.getFile().getPath()
                ), () -> areas.add(area));
    }

    public Area read(World world, @Nullable Area area, IO file) {
        var areaFile = new AreaFile(plugin, file, world, area);
        areaFile.saveIfAbsent();
        return areaFile.getArea();
    }

    public void save(World world) {
        var areas = this.areas.get(world);
        if (areas != null) areas.stream()
                .map(area -> new AreaFile(plugin, area))
                .forEach(AreaFile::save);
    }

    public void unload(World world) {
        areas.remove(world);
    }

    public boolean delete(Area area) {
        areas.computeIfPresent(area.getWorld(), (world, areas) -> {
            areas.remove(area);
            return areas.isEmpty() ? null : areas;
        });
        return area.getFile().delete();
    }
}
