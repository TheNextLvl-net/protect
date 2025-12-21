package net.thenextlvl.protect.adapters.area;

import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GroupedAreaAdapter extends RegionizedAreaAdapter<GroupedRegion, CraftGroupedArea> {
    public GroupedAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "grouped");
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftGroupedArea(plugin, world, name, tag);
    }
}
