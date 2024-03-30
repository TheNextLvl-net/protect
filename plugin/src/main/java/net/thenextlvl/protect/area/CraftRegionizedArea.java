package net.thenextlvl.protect.area;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaRedefineEvent;
import net.thenextlvl.protect.event.AreaSchematicDeleteEvent;
import net.thenextlvl.protect.event.AreaSchematicLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Getter
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public abstract class CraftRegionizedArea<T extends Region> extends CraftArea implements RegionizedArea<T> {
    private final File schematic;
    private T region;

    protected CraftRegionizedArea(ProtectPlugin plugin, @NamePattern String name, World world, T region, int priority) throws IllegalArgumentException {
        super(name, world, priority);
        this.schematic = new File(plugin.schematicFolder(), name + ".schem");
        this.region = region;
    }

    @Override
    public boolean redefine(T region) {
        if (!new AreaRedefineEvent(this, region).callEvent()) return false;
        this.region = region;
        return true;
    }

    @Override
    public boolean contains(Location location) {
        if (!getWorld().equals(location.getWorld())) return false;
        var position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return getRegion().contains(position);
    }

    @Override
    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    @Override
    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
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

    @Override
    public boolean isTooBig() {
        return getRegion().getVolume() >= 1_000_000_000;
    }

    @Override
    public boolean deleteSchematic() {
        return !getSchematic().exists() || (new AreaSchematicDeleteEvent(this).callEvent() && getSchematic().delete());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveSchematic() throws IOException, WorldEditException {
        var file = getSchematic().getAbsoluteFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (var editSession = WorldEdit.getInstance().newEditSession(getRegion().getWorld());
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
    public boolean loadSchematic() throws IOException, WorldEditException {
        Preconditions.checkNotNull(getRegion().getWorld(), "world");
        var event = new AreaSchematicLoadEvent(CraftRegionizedArea.this);
        if (!event.callEvent()) return false;
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(getRegion().getWorld())) {
            var clipboard = BuiltInClipboardFormat.FAST.getReader(new FileInputStream(getSchematic())).read();
            getRegion().getWorld().getEntities(getRegion()).forEach(com.sk89q.worldedit.entity.Entity::remove);
            var operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getRegion().getMinimumPoint()).
                    copyBiomes(true).copyEntities(true).ignoreAirBlocks(false).build();
            Operations.complete(operation);
            event.getSuccessListeners().forEach(consumer -> consumer.accept(CraftRegionizedArea.this));
            return true;
        }
    }
}
