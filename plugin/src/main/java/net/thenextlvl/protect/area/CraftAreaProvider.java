package net.thenextlvl.protect.area;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import core.paper.adapters.key.KeyAdapter;
import core.paper.adapters.world.LocationAdapter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.adapter.area.AreaTypeAdapter;
import net.thenextlvl.protect.adapter.flag.FlagAdapter;
import net.thenextlvl.protect.adapter.flag.FlagsAdapter;
import net.thenextlvl.protect.adapter.region.CuboidRegionAdapter;
import net.thenextlvl.protect.adapter.region.CylinderRegionAdapter;
import net.thenextlvl.protect.adapter.region.EllipsoidRegionAdapter;
import net.thenextlvl.protect.adapter.vector.BlockVectorAdapter;
import net.thenextlvl.protect.adapter.vector.Vector2Adapter;
import net.thenextlvl.protect.adapter.vector.Vector3Adapter;
import net.thenextlvl.protect.area.event.AreaLoadEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

@NullMarked
@RequiredArgsConstructor
public class CraftAreaProvider implements AreaProvider {
    private final Map<World, Set<FileIO<Area>>> areas = new HashMap<>();
    private final ProtectPlugin plugin;

    @Override
    public Stream<Area> getAreas() {
        return areas.values().stream()
                .flatMap(Collection::stream)
                .map(FileIO::getRoot);
    }

    @Override
    public Stream<Area> getAreas(World world) {
        return areas.getOrDefault(world, Collections.emptySet()).stream()
                .map(FileIO::getRoot);
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
        var files = areaFolder.listFiles((file, name) -> name.endsWith(".json"));
        if (files != null) for (var file : files) load(world, file);
        if (getAreas(world).anyMatch(area -> area instanceof GlobalArea)) return;
        persist(new CraftGlobalArea(plugin, world, Set.of(), null, new LinkedHashMap<>(), -1));
    }

    public void load(World world, File file) {
        var loaded = read(world, null, IO.of(file));
        new AreaLoadEvent(loaded.getRoot()).callEvent();
        memoize(world, loaded);
    }

    public void persist(Area area) {
        memoize(area.getWorld(), read(area.getWorld(), area, IO.of(area.getFile())).saveIfAbsent());
    }

    private void memoize(World world, FileIO<Area> file) {
        var areas = this.areas.computeIfAbsent(world, k -> new HashSet<>());
        areas.stream().filter(io -> io.getRoot().equals(file.getRoot())).findAny()
                .ifPresentOrElse(io -> plugin.getComponentLogger().warn(
                        "Ignoring duplicate area {}: {}",
                        file.getRoot().getName(), file.getIO()
                ), () -> areas.add(file));
    }

    public GsonFile<Area> read(World world, @Nullable Area area, IO file) {
        var gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter())
                .registerTypeHierarchyAdapter(CylinderRegion.class, new CylinderRegionAdapter())
                .registerTypeHierarchyAdapter(EllipsoidRegion.class, new EllipsoidRegionAdapter())
                .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
                .registerTypeHierarchyAdapter(Vector2.class, new Vector2Adapter())
                .registerTypeHierarchyAdapter(Vector3.class, new Vector3Adapter())
                .registerTypeHierarchyAdapter(Location.class, LocationAdapter.simple(world))
                .registerTypeHierarchyAdapter(NamespacedKey.class, KeyAdapter.bukkit())
                .registerTypeHierarchyAdapter(Area.class, new AreaTypeAdapter(plugin, world))
                .registerTypeHierarchyAdapter(Flag.class, new FlagAdapter(plugin))
                .registerTypeHierarchyAdapter(new TypeToken<Map<Flag<?>, Object>>() {
                }.getRawType(), new FlagsAdapter(plugin))
                .setPrettyPrinting().create();
        return area != null ? new GsonFile<>(file, area, gson) : new GsonFile<>(file, Area.class, gson);
    }

    public void save(World world) {
        var files = areas.get(world);
        if (files != null) files.forEach(FileIO::save);
    }

    public void unload(World world) {
        areas.remove(world);
    }

    public boolean delete(Area area) {
        areas.computeIfPresent(area.getWorld(), (world, areas) -> {
            areas.removeIf(file -> file.getRoot().equals(area));
            return areas.isEmpty() ? null : areas;
        });
        return area.getFile().delete();
    }
}
