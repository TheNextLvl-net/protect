package net.thenextlvl.protect.area;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import core.paper.adapters.key.KeyAdapter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.adapter.*;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
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

    @ApiStatus.Internal
    public void loadAreas(World world) {
        var areaFolder = new File(world.getWorldFolder(), "areas");
        var files = areaFolder.listFiles((file, name) -> name.endsWith(".json"));
        if (files != null) for (var file : files) loadArea(world, file);
        if (getAreas(world).anyMatch(area -> area instanceof GlobalArea)) return;
        loadArea(new CraftGlobalArea(world, -1));
    }

    @ApiStatus.Internal
    public void loadArea(Area area) {
        addAreaFile(area.getWorld(), loadFile(area.getWorld(), area, IO.of(area.getFile())).saveIfAbsent());
    }

    @ApiStatus.Internal
    public void loadArea(World world, File file) {
        addAreaFile(world, loadFile(world, null, IO.of(file)));
    }

    private void addAreaFile(World world, FileIO<Area> file) {
        var areas = this.areas.computeIfAbsent(world, k -> new HashSet<>());
        areas.stream().filter(io -> io.getRoot().equals(file.getRoot())).findAny()
                .ifPresentOrElse(io -> plugin.getComponentLogger().warn(
                        "Ignoring duplicate area {}: {}",
                        file.getRoot().getName(), file.getIO()
                ), () -> areas.add(file));
    }

    @ApiStatus.Internal
    public FileIO<Area> loadFile(World world, @Nullable Area area, IO file) {
        var gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter())
                .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
                .registerTypeHierarchyAdapter(NamespacedKey.class, KeyAdapter.Bukkit.INSTANCE)
                .registerTypeHierarchyAdapter(Area.class, new AreaTypeAdapter(plugin, world))
                .registerTypeHierarchyAdapter(Flag.class, new FlagAdapter(plugin))
                .registerTypeHierarchyAdapter(new TypeToken<Map<Flag<?>, Object>>() {
                }.getRawType(), new FlagsAdapter(plugin))
                .setPrettyPrinting().create();
        return area != null ? new GsonFile<>(file, area, gson) : new GsonFile<>(file, Area.class, gson);
    }

    @ApiStatus.Internal
    public void saveAreas(World world) {
        areas.get(world).forEach(FileIO::save);
    }

    @ApiStatus.Internal
    public void unloadAreas(World world) {
        areas.remove(world);
    }

    @ApiStatus.Internal
    public boolean deleteArea(Area area) {
        var areas = this.areas.get(area.getWorld());
        if (areas != null) areas.removeIf(file -> file.getRoot().equals(area));
        return area.getFile().delete();
    }
}
