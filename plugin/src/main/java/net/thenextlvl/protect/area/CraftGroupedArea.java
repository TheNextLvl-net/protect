package net.thenextlvl.protect.area;

import core.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftGroupedArea extends CraftRegionizedArea<GroupedRegion> implements GroupedArea {
    public CraftGroupedArea(ProtectPlugin plugin, AreaCreator<GroupedRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftGroupedArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected GroupedRegion readRegion(CompoundTag tag) {
        return plugin.nbt.fromTag(tag.getAsCompound("region"), GroupedRegion.class);
    }
}
