package net.thenextlvl.protect.area;

import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CraftGroupedArea extends CraftRegionizedArea<GroupedRegion> implements GroupedArea {
    public CraftGroupedArea(final ProtectPlugin plugin, final AreaCreator<GroupedRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftGroupedArea(final ProtectPlugin plugin, final World world, final String name, final CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected GroupedRegion readRegion(final CompoundTag tag) {
        return plugin.nbt.deserialize(tag.getAsCompound("region"), GroupedRegion.class);
    }
}
