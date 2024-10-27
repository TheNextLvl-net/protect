package net.thenextlvl.protect.area;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.inheritance.AreaParentChangeEvent;
import net.thenextlvl.protect.area.event.region.AreaRedefineEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicDeleteEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicLoadEvent;
import net.thenextlvl.protect.area.event.schematic.AreaSchematicLoadedEvent;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftRegionizedArea<T extends Region> extends CraftArea implements RegionizedArea<@NotNull T> {
    private final @NotNull File dataFolder = new File(getWorld().getWorldFolder(), "areas");
    private final @NotNull File file = new File(getDataFolder(), getName() + ".json");
    private final @NotNull File schematic;
    private @Nullable String parent;
    private T region;

    public CraftRegionizedArea(@NotNull ProtectPlugin plugin, @NotNull AreaCreator<T> creator) throws CircularInheritanceException {
        super(plugin, creator.name(), creator.world(), new HashSet<>(creator.members()),
                creator.owner(), new LinkedHashMap<>(creator.flags()), creator.priority());
        this.parent = creator.parent();
        this.region = creator.region();
        this.schematic = new File(plugin.schematicFolder(), creator.name() + ".schem");
    }

    @Override
    public @NotNull Optional<Area> getParent() {
        return parent().flatMap(plugin.areaProvider()::getArea);
    }

    public @NotNull Optional<String> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public @NotNull RegionSelector getRegionSelector() {
        return new CuboidRegionSelector(
                getRegion().getWorld(),
                getRegion().getMinimumPoint(),
                getRegion().getMaximumPoint()
        );
    }

    @Override
    public boolean setParent(@Nullable Area parent) throws CircularInheritanceException {
        if (parent != null && parent.getName().equals(this.parent)) return false;
        checkCircularInheritance(parent);
        var event = new AreaParentChangeEvent(this, parent);
        if (!event.callEvent()) return false;
        if (!Objects.equals(parent, event.getParent())) checkCircularInheritance(event.getParent());
        this.parent = event.getParent() != null ? event.getParent().getName() : null;
        return true;
    }

    public void checkCircularInheritance(@Nullable Area parent) throws CircularInheritanceException {
        var path = new LinkedList<Area>();
        while (parent != null) {
            if (parent == this) throw new CircularInheritanceException("Circular inheritance detected: ");
            parent = parent.getParent().orElse(null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean setRegion(@NotNull T region) {
        var event = new AreaRedefineEvent<>(this, (T) region.clone());
        if (event.callEvent()) this.region = event.getRegion();
        return !event.isCancelled();
    }

    @Override
    public boolean canInteract(@NotNull Area area) {
        return area instanceof RegionizedArea<?> regionized && Objects.equals(regionized.getOwner(), getOwner());
    }

    @Override
    public boolean isTooBig() {
        return getRegion().getVolume() >= 1_000_000_000;
    }

    @Override
    public boolean contains(@NotNull Location location) {
        return getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
            extent.setCopyingEntities(true);
            extent.setCopyingBiomes(true);
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
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            var clipboard = BuiltInClipboardFormat.FAST_V3.getReader(new FileInputStream(getSchematic())).read();
            world.getEntities(getRegion()).forEach(com.sk89q.worldedit.entity.Entity::remove);
            var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                    copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            return new AreaSchematicLoadedEvent(this).callEvent();
        }
    }

    @Override
    public @NotNull List<@NotNull Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(this::contains)
                .toList();
    }

    @Override
    public @NotNull List<@NotNull Player> getPlayers() {
        return getWorld().getPlayers().stream()
                .filter(this::contains)
                .toList();
    }
}
