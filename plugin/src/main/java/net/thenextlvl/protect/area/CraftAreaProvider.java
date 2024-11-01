package net.thenextlvl.protect.area;

import core.io.IO;
import core.nbt.NBTInputStream;
import core.nbt.NBTOutputStream;
import core.nbt.serialization.ParserException;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaLoadEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

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
        if (area == null) return read(file, world);
        if (file.exists()) return read(file, area);
        save(area);
        return area;
    }

    private NBTInputStream stream(IO file) throws IOException {
        return new NBTInputStream(
                file.inputStream(READ),
                StandardCharsets.UTF_8
        );
    }

    private Area read(IO file, World world) {
        try (var inputStream = stream(file)) {
            var entry = inputStream.readNamedTag();
            var root = entry.getKey().getAsCompound();
            var name = entry.getValue().orElseThrow(() -> new ParserException("Area misses root name"));
            var split = root.get("type").getAsString().split(":", 2);
            var type = new NamespacedKey(split[0], split[1]);
            return plugin.areaService().getAdapter(type)
                    .construct(world, name, root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Area read(IO file, Area area) {
        try (var inputStream = stream(file)) {
            var tag = inputStream.readNamedTag().getKey();
            area.deserialize(tag);
            return area;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(World world) {
        var areas = this.areas.get(world);
        if (areas != null) areas.forEach(this::save);
    }

    private void save(Area area) {
        try {
            var io = IO.of(area.getFile());
            io.createParents();
            try (var outputStream = new NBTOutputStream(
                    io.outputStream(WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            )) {
                outputStream.writeTag(area.getName(), area.serialize());
            }
        } catch (IOException e) {
            plugin.getComponentLogger().error("Failed to save area {}", area.getName(), e);
        }
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
