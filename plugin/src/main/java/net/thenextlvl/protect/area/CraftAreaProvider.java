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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static net.thenextlvl.protect.ProtectPlugin.ISSUES;

@NullMarked
public final class CraftAreaProvider implements AreaProvider {
    private static final String DATA_SUFFIX = ".dat";
    private static final String BACKUP_SUFFIX = ".dat_old";
    private static final String TEMP_SUFFIX = ".dat_tmp";
    private static final String BROKEN_SUFFIX = ".dat_broken";

    private static final Comparator<Area> PRIORITY = Comparator.comparingInt(Area::getPriority)
            .thenComparing(Area::getName, Comparator.reverseOrder());

    private final Map<UUID, GlobalArea> globalAreas = new HashMap<>();
    private final Map<UUID, Set<Area>> worldAreas = new HashMap<>();
    private final Map<String, Area> namedAreas = new HashMap<>();
    private final Set<Area> areas = new HashSet<>();
    private final ProtectPlugin plugin;

    public CraftAreaProvider(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Stream<Area> getAreas() {
        return areas.stream();
    }

    @Override
    public Stream<Area> getAreas(final World world) {
        final var areas = worldAreas.get(world.getUID());
        return areas != null ? areas.stream() : Stream.empty();
    }

    @Override
    public Stream<Area> getAreas(final Location location) {
        return getAreas(location.getWorld()).filter(area -> area.contains(location));
    }

    @Override
    public Area getArea(final Location location) {
        return getAreas(location)
                .max(PRIORITY)
                .orElseGet(() -> getArea(location.getWorld()));
    }

    @Override
    public Optional<Area> getArea(final String name) {
        return Optional.ofNullable(namedAreas.get(name));
    }

    @Override
    public GlobalArea getArea(final World world) {
        return globalAreas.get(world.getUID());
    }

    public void load(final World world) {
        final var uuid = world.getUID();
        final var existing = globalAreas.get(uuid);
        if (existing != null) {
            plugin.getComponentLogger().warn("Tried to load global area {} twice", existing.getName());
            return;
        }
        final var folder = ProtectPlugin.getDataFolder(world);
        final var areas = new HashSet<Area>();
        GlobalArea globalArea = null;

        for (final var name : listAreaFiles(world, folder)) {
            final var file = folder.resolve(name + DATA_SUFFIX);
            final var area = read(world, file);
            if (area == null) continue;
            if (area instanceof final GlobalArea global) {
                if (globalArea == null) {
                    globalArea = global;
                    continue;
                }
            } else if (!namedAreas.containsKey(area.getName()) && areas.add(area)) continue;
            plugin.getComponentLogger().warn("Ignoring duplicate area {}: {}", area.getName(), file);
        }

        if (globalArea == null) globalArea = createGlobalArea(world);
        areas.add(globalArea);
        globalAreas.put(uuid, globalArea);
        worldAreas.put(uuid, areas);
        areas.forEach(area -> namedAreas.put(area.getName(), area));
        this.areas.addAll(areas);

        areas.forEach(area -> new AreaLoadEvent(area).callEvent());
    }

    private GlobalArea createGlobalArea(final World world) {
        final var globalArea = new CraftGlobalArea(plugin, world);
        preserveBrokenFile(globalArea);
        save(globalArea);
        return globalArea;
    }

    private void preserveBrokenFile(final Area area) {
        final var file = area.getDataFile();
        if (!Files.isRegularFile(file)) return;
        final var broken = file.resolveSibling(area.getName() + BROKEN_SUFFIX);
        try {
            Files.copy(file, broken, StandardCopyOption.REPLACE_EXISTING);
            plugin.getComponentLogger().warn("Replaced broken global area {}, the old file was kept as {}", area.getName(), broken);
        } catch (final IOException e) {
            plugin.getComponentLogger().error("Failed to back up broken global area file {}", file, e);
        }
    }

    private Set<String> listAreaFiles(final World world, final Path folder) {
        if (!Files.isDirectory(folder)) return Set.of();
        try (final var files = Files.list(folder)) {
            final var names = new LinkedHashSet<String>();
            files.map(path -> path.getFileName().toString()).forEach(file -> {
                if (file.endsWith(DATA_SUFFIX)) names.add(file.substring(0, file.length() - DATA_SUFFIX.length()));
                else if (file.endsWith(BACKUP_SUFFIX))
                    names.add(file.substring(0, file.length() - BACKUP_SUFFIX.length()));
            });
            return names;
        } catch (final IOException e) {
            plugin.getComponentLogger().error("Failed to list areas for {}", world.key().asString(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
            return Set.of();
        }
    }

    private @Nullable Area read(final World world, final Path file) {
        try {
            return readWithBackup(world, file);
        } catch (final EOFException e) {
            plugin.getComponentLogger().error("The area file {} is irrecoverably broken", file);
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to load area from {}", file, e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
        }
        return null;
    }

    private Area readWithBackup(final World world, final Path file) throws IOException {
        try {
            return read(world, NBTInputStream.create(file));
        } catch (final Exception e) {
            final var backup = backupFile(file);
            if (!Files.isRegularFile(backup)) throw e;
            if (Files.isRegularFile(file)) plugin.getComponentLogger().warn("Failed to load area from {}", file);
            plugin.getComponentLogger().warn("Falling back to {}", backup);
            return read(world, NBTInputStream.create(backup));
        }
    }

    private Area read(final World world, final NBTInputStream stream) throws IOException {
        try (final var inputStream = stream) {
            final var entry = inputStream.readNamedTag();
            final var tag = entry.getValue();
            final var name = entry.getKey();
            final var type = plugin.nbt.deserialize(tag.get("type"), Key.class);
            return plugin.areaService().getAdapter(type).construct(world, name, tag);
        }
    }

    public void persist(final Area area) throws IOException {
        if (namedAreas.containsKey(area.getName()))
            throw new IllegalStateException("An area named " + area.getName() + " already exists");
        if (!add(area)) throw new IllegalStateException(
                "The world " + area.getWorld().key().asString() + " is not loaded"
        );
        try {
            write(area);
        } catch (final IOException | RuntimeException e) {
            remove(area);
            throw e;
        }
    }

    public void save(final World world) {
        final var areas = worldAreas.get(world.getUID());
        if (areas != null) areas.forEach(this::save);
    }

    private void save(final Area area) {
        try {
            write(area);
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to save area {}", area.getName(), e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
        }
    }

    private void write(final Area area) throws IOException {
        final var file = area.getDataFile();
        final var directory = area.getDataPath();
        Files.createDirectories(directory);
        final var temp = Files.createTempFile(directory, area.getName(), TEMP_SUFFIX);
        try {
            try (final var outputStream = NBTOutputStream.create(temp)) {
                outputStream.writeTag(area.getName(), area.serialize());
            }
            if (Files.isRegularFile(file)) Files.move(file, area.getBackupFile(), StandardCopyOption.REPLACE_EXISTING);
            move(temp, file);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static void move(final Path source, final Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (final AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path backupFile(final Path file) {
        final var name = file.getFileName().toString();
        final var base = name.endsWith(DATA_SUFFIX) ? name.substring(0, name.length() - DATA_SUFFIX.length()) : name;
        return file.resolveSibling(base + BACKUP_SUFFIX);
    }

    private boolean add(final Area area) {
        final var areas = worldAreas.get(area.getWorld().getUID());
        if (areas == null || !areas.add(area)) return false;
        namedAreas.put(area.getName(), area);
        this.areas.add(area);
        return true;
    }

    private boolean remove(final Area area) {
        final var areas = worldAreas.get(area.getWorld().getUID());
        if (areas == null || !areas.remove(area)) return false;
        namedAreas.remove(area.getName(), area);
        this.areas.remove(area);
        return true;
    }

    public void unload(final World world) {
        globalAreas.remove(world.getUID());
        final var areas = worldAreas.remove(world.getUID());
        if (areas != null) areas.forEach(area -> {
            namedAreas.remove(area.getName(), area);
            this.areas.remove(area);
        });
    }

    public boolean delete(final Area area) {
        if (area instanceof GlobalArea) return false;
        final var removed = remove(area);
        try {
            final var deleted = Files.deleteIfExists(area.getDataFile())
                    | Files.deleteIfExists(area.getBackupFile());
            return removed || deleted;
        } catch (final IOException e) {
            if (removed) add(area);
            plugin.getComponentLogger().warn("Failed to delete area {}", area.getName());
            plugin.getComponentLogger().warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
            return false;
        }
    }
}
