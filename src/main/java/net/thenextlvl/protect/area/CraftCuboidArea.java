package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(final ProtectPlugin plugin, final AreaCreator<CuboidRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftCuboidArea(final ProtectPlugin plugin, final World world, final String name, final CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected CuboidRegion readRegion(final CompoundTag tag) {
        return plugin.nbt.deserialize(tag.getAsCompound("region"), CuboidRegion.class);
    }
}
