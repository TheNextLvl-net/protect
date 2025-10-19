package net.thenextlvl.protect.area;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.inheritance.AreaParentChangeEvent;
import net.thenextlvl.protect.area.event.region.AreaRedefineEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicDeleteEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicLoadEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicLoadedEvent;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public abstract class CraftRegionizedArea<T extends Region> extends CraftArea implements RegionizedArea<T> {
    private final File dataFolder = new File(getWorld().getWorldFolder(), "areas");
    private final File fallbackFile = new File(getDataFolder(), getName() + ".dat_old");
    private final File file = new File(getDataFolder(), getName() + ".dat");
    private final File schematic = new File(plugin.schematicFolder(), getName() + ".schem");
    private @Nullable String parent;
    private T region;

    public CraftRegionizedArea(ProtectPlugin plugin, AreaCreator<T> creator) throws CircularInheritanceException {
        super(plugin, creator.name(), creator.world(), creator.members(),
                creator.owner(), creator.flags(), creator.priority());
        this.parent = creator.parent();
        this.region = creator.region();
    }

    public CraftRegionizedArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        super(plugin, world, name, tag);
        this.parent = readParent(tag);
        this.region = readRegion(tag);
    }

    protected abstract T readRegion(CompoundTag tag);

    private void writeRegion(CompoundTag tag) {
        tag.add("region", plugin.nbt.serialize(region));
    }

    @Override
    public Location getLocation() {
        var center = getRegion().getCenter();
        var location = new Location(getWorld(), center.x() + 0.5, getRegion().getMinimumY(), center.z() + 0.5);
        if (getRegion().getLength() < getRegion().getWidth()) {
            location.setZ(location.getZ() - (getRegion().getLength() / 2d) - 0.5);
        } else {
            location.setX(location.getX() - (getRegion().getWidth() / 2d) - 0.5);
            location.setYaw(-90);
        }

        location.setY(getSafeHeight(location.getBlock()));

        var block = location.getBlock();
        var box = block.getCollisionShape().getBoundingBoxes().stream()
                .max(Comparator.comparingDouble(BoundingBox::getMaxY))
                .orElse(null);
        location.setY(box != null ? block.getY() + box.getMaxY() : block.getY());
        return location;
    }

    private int getSafeHeight(Block block) {
        if (block.isPassable()) {
            for (var y = block.getY(); y >= block.getWorld().getMinHeight(); y--) {
                if (block.getWorld().getBlockAt(block.getX(), y, block.getZ()).isPassable()) continue;
                return y + 1;
            }
        } else {
            for (var y = block.getY(); y <= block.getWorld().getMaxHeight(); y++) {
                if (!block.getWorld().getBlockAt(block.getX(), y, block.getZ()).isPassable()) continue;
                return y;
            }
        }
        return block.getWorld().getHighestBlockYAt(block.getX(), block.getZ());
    }

    @Override
    public Optional<Area> getParent() {
        return parent().flatMap(plugin.areaProvider()::getArea);
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public File getFallbackFile() {
        return fallbackFile;
    }

    public Optional<String> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public RegionSelector getRegionSelector() {
        return new CuboidRegionSelector(
                getRegion().getWorld(),
                getRegion().getMinimumPoint(),
                getRegion().getMaximumPoint()
        );
    }

    @Override
    public T getRegion() {
        return region;
    }

    @Override
    public boolean setParent(@Nullable Area parent) throws CircularInheritanceException {
        if (parent != null && parent.getName().equals(this.parent)) return false;
        if (parent != null) checkCircularInheritance(parent);
        var event = new AreaParentChangeEvent(this, parent);
        if (!event.callEvent()) return false;
        if (!Objects.equals(parent, event.getParent()) && event.getParent() != null)
            checkCircularInheritance(event.getParent());
        this.parent = event.getParent() != null ? event.getParent().getName() : null;
        return true;
    }

    public void checkCircularInheritance(Area parent) throws CircularInheritanceException {
        var path = new LinkedList<Area>();
        path.add(this);
        path.add(parent);
        do {
            if (parent == this) throw new CircularInheritanceException("Circular inheritance detected: " +
                    path.stream().map(Area::getName).collect(Collectors.joining(" -> ")));
            parent = parent.getParent().orElse(null);
            if (parent != null) path.add(parent);
        } while (parent != null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean setRegion(T region) {
        var event = new AreaRedefineEvent<>(this, (T) region.clone());
        if (event.callEvent()) this.region = event.getRegion();
        return !event.isCancelled();
    }

    @Override
    public boolean canInteract(Area area) {
        return equals(area) || area.getParent().map(this::equals).orElse(false)
               || (area instanceof RegionizedArea<?> regionized && Objects.equals(regionized.getOwner(), getOwner()));
    }

    @Override
    public boolean isTooBig() {
        return getRegion().getVolume() >= 1_000_000_000;
    }

    @Override
    public boolean contains(Location location) {
        return getWorld().equals(location.getWorld())
                && getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public File getSchematic() {
        return schematic;
    }

    @Override
    public boolean deleteSchematic() {
        return getSchematic().exists() && (new AreaSchematicDeleteEvent(this).callEvent() && getSchematic().delete());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveSchematic() throws IOException, WorldEditException {
        var file = getSchematic().getAbsoluteFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (var editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(getWorld()));
             var writer = BuiltInClipboardFormat.FAST_V3.getWriter(new FileOutputStream(getSchematic()))) {
            var clipboard = new BlockArrayClipboard(getRegion());
            var extent = new ForwardExtentCopy(editSession, getRegion(), clipboard, getRegion().getMinimumPoint());
            extent.setCopyingBiomes(getRegion() instanceof FlatRegion);
            extent.setCopyingEntities(true);
            Operations.complete(extent);
            writer.write(clipboard);
        }
    }

    @Override
    public boolean loadSchematic() throws IOException, WorldEditException {
        if (!getSchematic().isFile()) return false;
        var event = new AreaSchematicLoadEvent(this);
        if (!event.callEvent()) return false;
        var world = BukkitAdapter.adapt(getWorld());
        try (var editSession = WorldEdit.getInstance().newEditSession(world)) {
            var clipboard = BuiltInClipboardFormat.FAST_V3.getReader(new FileInputStream(getSchematic())).read();
            world.getEntities(getRegion()).forEach(com.sk89q.worldedit.entity.Entity::remove);
            var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                    copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            return new AreaSchematicLoadedEvent(this).callEvent();
        }
    }

    @Override
    public List<Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(this::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Player> getPlayers() {
        return getWorld().getPlayers().stream()
                .filter(this::contains)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Entity> getHighestEntities() {
        return getWorld().getEntities().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Player> getHighestPlayers() {
        return getWorld().getPlayers().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public CompoundTag serialize() {
        var tag = super.serialize();
        writeParent(tag);
        writeRegion(tag);
        return tag;
    }

    private void writeParent(CompoundTag tag) {
        if (parent != null) tag.add("parent", parent);
    }

    private @Nullable String readParent(CompoundTag tag) {
        return tag.optional("parent").map(Tag::getAsString).orElse(null);
    }
}
