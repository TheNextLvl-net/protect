package net.thenextlvl.protect.area;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.thenextlvl.protect.event.AreaOwnerChangeEvent;
import net.thenextlvl.protect.event.AreaRedefineEvent;
import net.thenextlvl.protect.event.AreaSchematicDeleteEvent;
import net.thenextlvl.protect.event.AreaSchematicLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Getter
@TypesAreNotNullByDefault
@ToString(callSuper = true)
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
@EqualsAndHashCode(callSuper = true)
public abstract class CraftRegionizedArea<T extends Region> extends CraftArea implements RegionizedArea<T> {
    private final File dataFolder = new File(getWorld().getWorldFolder(), "areas");
    private final File file = new File(getDataFolder(), getName() + ".json");
    private final File schematic;
    public @Nullable UUID owner;
    private T region;

    protected CraftRegionizedArea(File schematicFolder, @NamePattern.Regionized String name, World world, T region, int priority) {
        super(name, world, priority);
        this.schematic = new File(schematicFolder, name + ".schem");
        this.region = region;
    }

    @Override
    @NamePattern.Regionized
    @SuppressWarnings("PatternValidation")
    public String getName() {
        return super.getName();
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
    @SuppressWarnings("unchecked")
    public boolean setRegion(T region) {
        var clone = (T) region.clone();
        if (!new AreaRedefineEvent<>(this, clone).callEvent()) return false;
        this.region = clone;
        return true;
    }

    @Override
    public boolean hasOwner() {
        return getOwner() != null;
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        var event = new AreaOwnerChangeEvent<>(this, owner);
        if (event.callEvent()) internalSetOwner(event.getNewOwner());
    }

    public void internalSetOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    @Override
    public boolean isTooBig() {
        return getRegion().getVolume() >= 1_000_000_000;
    }

    @Override
    public boolean contains(Location location) {
        return getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean deleteSchematic() {
        return getSchematic().exists() && (new AreaSchematicDeleteEvent<>(this).callEvent() && getSchematic().delete());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveSchematic() throws IOException, WorldEditException {
        var file = getSchematic().getAbsoluteFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (var editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(getWorld()));
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
        var event = new AreaSchematicLoadEvent<>(this);
        if (!event.callEvent()) return false;
        var world = new BukkitWorld(getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            var clipboard = BuiltInClipboardFormat.FAST_V3.getReader(new FileInputStream(getSchematic())).read();
            world.getEntities(getRegion()).forEach(com.sk89q.worldedit.entity.Entity::remove);
            var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                    copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            event.getSuccessListeners().forEach(consumer -> consumer.accept(this));
            return true;
        }
    }

    @Override
    public List<Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(this::contains)
                .toList();
    }

    @Override
    public List<Player> getPlayers() {
        return getWorld().getPlayers().stream()
                .filter(this::contains)
                .toList();
    }
}
