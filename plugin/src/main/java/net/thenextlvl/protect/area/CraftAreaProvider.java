package net.thenextlvl.protect.area;

import core.io.IO;
import core.nbt.NBTInputStream;
import core.nbt.NBTOutputStream;
import core.nbt.serialization.ParserException;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@NullMarked
@RequiredArgsConstructor
public class CraftAreaProvider implements AreaProvider {
    private static final String ISSUES = "https://github.com/TheNextLvl-net/protect/issues/new";

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
                .orElseThrow(() -> new IllegalStateException("No global area found for " + world.key().asString()));
    }

    public void load(World world) {
        try {
            var areaFolder = new File(world.getWorldFolder(), "areas");
            var files = areaFolder.listFiles((file, name) -> name.endsWith(".dat"));
            if (files != null) for (var file : files) load(world, file);
            if (getAreas(world).anyMatch(area -> area instanceof GlobalArea)) return;
            persist(new CraftGlobalArea(plugin, world));
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to load areas for {}", world.key().asString(), e);
        }
    }

    private void load(World world, File file) {
        try {
            var loaded = read(world, null, file);
            new AreaLoadEvent(loaded).callEvent();
            memoize(world, loaded);
        } catch (EOFException e) {
            plugin.getComponentLogger().error("The area file {} is irrecoverably broken", file.getPath());
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to load area from {}", file.getPath(), e);
            plugin.getComponentLogger().error("Please report this issue on GitHub: {}", ISSUES);
        }
    }

    public void persist(Area area) throws IOException {
        memoize(area.getWorld(), read(area.getWorld(), area, area.getFile()));
    }

    private void memoize(World world, Area area) {
        var areas = this.areas.computeIfAbsent(world, k -> new HashSet<>());
        areas.stream().filter(area::equals).findAny()
                .ifPresentOrElse(io -> plugin.getComponentLogger().warn(
                        "Ignoring duplicate area {}: {}",
                        area.getName(), area.getFile().getPath()
                ), () -> areas.add(area));
    }

    private Area read(World world, @Nullable Area area, File file) throws IOException {
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

    private Area read(File file, World world) throws IOException {
        try (var inputStream = stream(IO.of(file))) {
            return read(world, inputStream);
        } catch (Exception e) {
            var io = IO.of(file.getPath() + "_old");
            if (!io.exists()) throw e;
            plugin.getComponentLogger().warn("Failed to load area from {}", file.getPath());
            plugin.getComponentLogger().warn("Falling back to {}", io);
            try (var inputStream = stream(io)) {
                return read(world, inputStream);
            }
        }
    }

    private Area read(World world, NBTInputStream inputStream) throws IOException {
        var entry = inputStream.readNamedTag();
        var root = entry.getKey().getAsCompound();
        var name = entry.getValue().orElseThrow(() -> new ParserException("Area misses root name"));
        var type = plugin.nbt.fromTag(root.get("type"), Key.class);
        return plugin.areaService().getAdapter(type).construct(world, name, root);
    }

    private Area read(File file, Area area) throws IOException {
        try (var inputStream = stream(IO.of(file))) {
            var tag = inputStream.readNamedTag().getKey();
            area.deserialize(tag);
            return area;
        }
    }

    public void save(World world) {
        var areas = this.areas.get(world);
        if (areas != null) areas.forEach(this::save);
    }

    private void save(Area area) {
        try {
            var io = IO.of(area.getFile());
            if (io.exists()) Files.move(area.getFile().toPath(),
                    Path.of(area.getFile().getPath() + "_old"),
                    StandardCopyOption.REPLACE_EXISTING);
            else io.createParents();
            try (var outputStream = new NBTOutputStream(
                    io.outputStream(WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            )) {
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
        areas.computeIfPresent(area.getWorld(), (world, areas) -> {
            areas.remove(area);
            return areas.isEmpty() ? null : areas;
        });
        return area.getFile().delete();
    }
}
