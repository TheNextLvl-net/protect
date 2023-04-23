package net.thenextlvl.protect.area;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import core.annotation.ParametersAreNonnullByDefault;
import core.api.file.format.GsonFile;
import lombok.*;
import net.thenextlvl.protect.adapter.FlagsAdapter;
import net.thenextlvl.protect.adapter.RegionAdapter;
import net.thenextlvl.protect.event.*;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.Flags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ToString(doNotUseGetters = true)
public sealed class Area implements Abilities, Container permits GlobalArea {
    @Nullable
    private static GsonFile<List<Area>> saves;

    public static GsonFile<List<Area>> getSaves() {
        return Preconditions.checkNotNull(saves, "areas are not initialized yet");
    }

    public static boolean isInitialized() {
        return saves != null;
    }

    public static void init() {
        Preconditions.checkArgument(!isInitialized(), "Area#init called twice");
        saves = new GsonFile<>(new File("plugins/Protect", "areas.json"), new ArrayList<>(), new TypeToken<>(){}) {
            @Override
            public GsonBuilder load(GsonBuilder builder) {
                builder.excludeFieldsWithoutExposeAnnotation();
                builder.registerTypeHierarchyAdapter(Region.class, new RegionAdapter());
                builder.registerTypeAdapter(Flags.class, new FlagsAdapter());
                return builder;
            }
        };
        Bukkit.getWorlds().forEach(Area::get);
    }

    private @Expose final String name;
    @EqualsAndHashCode.Exclude
    private @Expose int priority;
    private @Expose Region region;

    @Nullable
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Schematic schematic;
    @EqualsAndHashCode.Exclude
    private @Expose final Flags flags = new Flags();

    protected Area(String name, Region region) throws IllegalArgumentException {
        Preconditions.checkArgument(region.getWorld() instanceof BukkitWorld, "expected bukkit world");
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name;
    }

    public boolean isGlobalArea() {
        return false;
    }

    @Override
    public boolean isTooBig() {
        return getRegion().getLength() >= 10000000;
    }

    public boolean delete() {
        if (!getSchematic().delete()) return false;
        if (!new AreaDeleteEvent(this).callEvent()) return false;
        getSaves().getRoot().remove(this);
        return true;
    }

    public boolean redefine(Region region) {
        if (!(region.getWorld() instanceof BukkitWorld)) return false;
        if (!new AreaRedefineEvent(this, region).callEvent()) return false;
        setRegion(region);
        return true;
    }

    public <T> T getFlag(Flag<T> flag) {
        return (T) flags.getOrDefault(flag, flag.defaultValue());
    }

    public <T> boolean setFlag(Flag<T> flag, T value) {
        if (getFlag(flag).equals(value)) return false;
        flags.put(flag, value);
        return true;
    }

    public <T> boolean isDefault(Flag<T> flag) {
        return getFlag(flag).equals(flag.defaultValue());
    }

    public boolean hasFlag(Flag<?> flag) {
        return flags.containsKey(flag);
    }

    public boolean unsetFlag(Flag<?> flag) {
        if (!hasFlag(flag)) return false;
        flags.remove(flag);
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location, Material material) {
        return getFlag(Flag.PLACE).test(player, location, material);
    }

    @Override
    public boolean canBreak(Player player, Location location, Material material) {
        return getFlag(Flag.BREAK).test(player, location, material);
    }

    @Override
    public boolean canUse(Player player, Location location, Material material) {
        return getFlag(Flag.USE).test(player, location, material);
    }

    @Override
    public boolean contains(Location location) {
        if (!getWorld().equals(location.getWorld())) return false;
        BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return getRegion().contains(position);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) if (equals(highestArea(all))) players.add(all);
        return players;
    }

    public World getWorld() {
        return ((BukkitWorld) Objects.requireNonNull(getRegion().getWorld())).getWorld();
    }

    public Schematic getSchematic() {
        if (schematic == null) schematic = new Schematic();
        return schematic;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final class Schematic {
        public static final File DATA_FOLDER = new File("plugins/Protect/schematics");
        private final File file = new File(DATA_FOLDER, getName() + ".schem");

        public boolean load() throws WorldEditException, IOException {
            if (!getFile().isFile()) return false;
            Preconditions.checkArgument(getRegion().getWorld() instanceof BukkitWorld);
            var event = new AreaSchematicLoadEvent(Area.this);
            if (!event.callEvent()) return false;
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(getRegion().getWorld())) {
                var clipboard = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(getFile())).read();
                getRegion().getWorld().getEntities(getRegion()).forEach(Entity::remove);
                var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                        copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
                Operations.complete(operation);
                event.getSuccessListeners().forEach(consumer -> consumer.accept(Area.this));
                return true;
            }
        }

        public boolean save() throws IOException, WorldEditException {
            var clipboard = new BlockArrayClipboard(getRegion());
            try (var editSession = WorldEdit.getInstance().newEditSession(getRegion().getWorld())) {
                var extent = new ForwardExtentCopy(editSession, getRegion(), clipboard, getRegion().getMinimumPoint());
                extent.setCopyingEntities(true);
                extent.setCopyingBiomes(true);
                Operations.complete(extent);
                createFile();
                try (var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(getFile()))) {
                    writer.write(clipboard);
                    return true;
                }
            }
        }

        @Nullable
        public Clipboard getSchematic() throws IOException {
            if (!getFile().isFile()) return null;
            try (var reader = BuiltInClipboardFormat.MCEDIT_SCHEMATIC.getReader(new FileInputStream(getFile()))) {
                return reader.read();
            }
        }

        public boolean delete() {
            return !getFile().exists() || (new AreaSchematicDeleteEvent(Area.this).callEvent() && file.delete());
        }

        private void createFile() throws IOException {
            var file = getFile().getAbsoluteFile();
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public static Stream<Area> areas() {
        return Streams.concat(getSaves().getRoot().stream(), GlobalArea.areas.values().stream());
    }

    public static Stream<Area> areas(World world) {
        return areas().filter(area -> area.getWorld().equals(world));
    }

    public static Stream<Area> areas(Location location) {
        return areas(location.getWorld()).filter(area -> area.contains(location));
    }

    public static Stream<Area> areas(Block block) {
        return areas(block.getLocation());
    }

    public static Stream<Area> areas(org.bukkit.entity.Entity entity) {
        return areas(entity.getLocation());
    }

    public static Area highestArea(org.bukkit.entity.Entity entity) {
        return highestArea(entity.getLocation());
    }

    public static Area highestArea(Location location) {
        return areas(location).max(Comparator.comparingInt(Area::getPriority)).orElseGet(() -> get(location.getWorld()));
    }

    public static Area highestArea(Block block) {
        return highestArea(block.getLocation());
    }

    @Nullable
    public static Area get(String name) {
        for (Area area : getSaves().getRoot()) if (area.getName().equals(name)) return area;
        if (name.length() <= 4) return null;
        World world = Bukkit.getWorld(name.substring(2, name.length() - 2));
        return world != null ? get(world) : null;
    }

    public static GlobalArea get(World world) {
        var areas = GlobalArea.areas;
        if (!areas.containsKey(world)) areas.put(world, new GlobalArea(world));
        return areas.get(world);
    }

    public static Area create(String name, World world, BlockVector3 pos1, BlockVector3 pos2) {
        return create(name, new CuboidRegion(new BukkitWorld(world), pos1, pos2));
    }

    public static Area create(String name, Region region) {
        Area area = new Area(name, region);
        new AreaCreateEvent(area).callEvent();
        getSaves().getRoot().add(area);
        return area;
    }
}
