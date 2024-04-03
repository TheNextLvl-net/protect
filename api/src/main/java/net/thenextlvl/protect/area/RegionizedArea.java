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
import com.sk89q.worldedit.session.ClipboardHolder;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.event.AreaSchematicDeleteEvent;
import net.thenextlvl.protect.event.AreaSchematicLoadEvent;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * The RegionizedArea interface represents an area that is bound to a region.
 * It extends the Area interface and the SchematicHolder interface.
 *
 * @param <T> The type of region associated with this area.
 */
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface RegionizedArea<T extends Region> extends Area, SchematicHolder {

    /**
     * Retrieves the region associated with this area.
     *
     * @return The region associated with this area.
     */
    T getRegion();

    /**
     * Sets the region associated with this {@link RegionizedArea}.
     *
     * @param region the region to set
     * @return true if the region was successfully set, false otherwise
     */
    boolean setRegion(T region);

    /**
     * Checks if the area is too big.
     *
     * @return true if the area is too big, false otherwise
     */
    default boolean isTooBig() {
        return getRegion().getVolume() >= 1_000_000_000;
    }

    @Override
    default boolean deleteSchematic() {
        return getSchematic().exists() && (new AreaSchematicDeleteEvent<>(this).callEvent() && getSchematic().delete());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void saveSchematic() throws IOException, WorldEditException {
        var file = getSchematic().getAbsoluteFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (var editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(getWorld()));
             var writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(getSchematic()))) {
            var clipboard = new BlockArrayClipboard(getRegion());
            var extent = new ForwardExtentCopy(editSession, getRegion(), clipboard, getRegion().getMinimumPoint());
            extent.setCopyingEntities(true);
            extent.setCopyingBiomes(true);
            Operations.complete(extent);
            writer.write(clipboard);
        }
    }

    @Override
    default boolean loadSchematic() throws IOException, WorldEditException {
        if (!getSchematic().isFile()) return false;
        var event = new AreaSchematicLoadEvent<>(this);
        if (!event.callEvent()) return false;
        var world = new BukkitWorld(getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            var clipboard = BuiltInClipboardFormat.FAST.getReader(new FileInputStream(getSchematic())).read();
            world.getEntities(getRegion()).forEach(com.sk89q.worldedit.entity.Entity::remove);
            var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                    copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            event.getSuccessListeners().forEach(consumer -> consumer.accept(this));
            return true;
        }
    }

    @Override
    default boolean contains(Location location) {
        return getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    default List<Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(this::contains)
                .toList();
    }

    @Override
    default List<Player> getPlayers() {
        return getWorld().getPlayers().stream()
                .filter(this::contains)
                .toList();
    }
}
