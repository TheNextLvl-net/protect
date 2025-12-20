package net.thenextlvl.protect.area;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.NBTInputStream;
import net.thenextlvl.nbt.NBTOutputStream;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@NullMarked
public final class CraftAreaProvider implements AreaProvider {
    private static final String ISSUES = "https://github.com/TheNextLvl-net/protect/issues/new";

    private final Map<World, Set<Area>> areas = new HashMap<>();
    private final ProtectPlugin plugin;

    public CraftAreaProvider(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Stream<Area> getAreas() {
        return areas.values().stream().flatMap(Collection::stream);
    }

    @Override
    public Stream<Area> getAreas(World world) {
        var areas = this.areas.get(world);
        return areas != null ? areas.stream() : Stream.empty();
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
                .orElseThrow(() -> new IllegalStateException("No global area found for " + world.key().asString()));
    }

    public void load(World world) {
        var areaFolder = world.getWorldFolder().toPath().resolve("areas");
        try (var files = Files.list(areaFolder).filter(path -> path.getFileName().toString().endsWith(".dat"))) {
            files.forEach(file -> load(world, file));
            if (getAreas(world).anyMatch(area -> area instanceof GlobalArea)) return;
            persist(new CraftGlobalArea(plugin, world));
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to load areas for {}", world.key().asString(), e);
        }
    }

    private void load(World world, Path file) {
        try {
            var loaded = read(world, null, file);
            new AreaLoadEvent(loaded).callEvent();
            memoize(world, loaded);
        } catch (EOFException e) {
            plugin.getComponentLogger().error("The area file {} is irrecoverably broken", file);
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to load area from {}", file, e);
            plugin.getComponentLogger().error("Please report this issue on GitHub: {}", ISSUES);
        }
    }

    public void persist(Area area) throws IOException {
        memoize(area.getWorld(), read(area.getWorld(), area, area.getDataFile()));
    }

    private void memoize(World world, Area area) {
        var areas = this.areas.computeIfAbsent(world, k -> new HashSet<>());
        areas.stream().filter(area::equals).findAny()
                .ifPresentOrElse(io -> plugin.getComponentLogger().warn(
                        "Ignoring duplicate area {}: {}",
                        area.getName(), area.getDataFile()
                ), () -> areas.add(area));
    }

    private Area read(World world, @Nullable Area area, Path file) throws IOException {
        if (area == null) return read(file, world);
        if (Files.isRegularFile(file)) return read(file, area);
        save(area);
        return area;
    }

    private Area read(Path file, World world) throws IOException {
        try (var inputStream = NBTInputStream.create(file)) {
            return read(world, inputStream);
        } catch (Exception e) {
            var backup = file.resolveSibling(file.getFileName() + "_old");
            if (!Files.isRegularFile(backup)) throw e;
            plugin.getComponentLogger().warn("Failed to load area from {}", file);
            plugin.getComponentLogger().warn("Falling back to {}", backup);
            try (var inputStream = NBTInputStream.create(backup)) {
                return read(world, inputStream);
            }
        }
    }

    private Area read(World world, NBTInputStream inputStream) throws IOException {
        var entry = inputStream.readNamedTag();
        var tag = entry.getValue();
        var name = entry.getKey();
        var type = plugin.nbt.deserialize(tag.get("type"), Key.class);
        return plugin.areaService().getAdapter(type).construct(world, name, tag);
    }

    private Area read(Path file, Area area) throws IOException {
        try (var inputStream = NBTInputStream.create(file)) {
            area.deserialize(inputStream.readTag());
            return area;
        }
    }

    public void save(World world) {
        var areas = this.areas.get(world);
        if (areas != null) areas.forEach(this::save);
    }

    private void save(Area area) {
        try {
            var file = area.getDataFile();
            if (Files.isRegularFile(file)) Files.move(file,
                    area.getBackupFile(),
                    StandardCopyOption.REPLACE_EXISTING);
            else Files.createDirectories(file.toAbsolutePath().getParent());
            try (var outputStream = NBTOutputStream.create(file)) {
                outputStream.writeTag(area.getName(), area.serialize());
            }
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to save area {}", area.getName(), e);
            plugin.getComponentLogger().error("Please report this issue on GitHub: {}", ISSUES);
        }
    }

    public void unload(World world) {
        areas.remove(world);
    }

    public boolean delete(Area area) {
        try {
            if (!(Files.deleteIfExists(area.getDataFile()) | Files.deleteIfExists(area.getBackupFile()))) return false;
            areas.computeIfPresent(area.getWorld(), (world, areas) -> {
                areas.remove(area);
                return areas.isEmpty() ? null : areas;
            });
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
