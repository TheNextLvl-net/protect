package net.thenextlvl.protect.adapter.area;

import core.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GroupedAreaAdapter extends RegionizedAreaAdapter<GroupedRegion, CraftGroupedArea> {
    public GroupedAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "grouped");
    }

    @Override
    protected CraftGroupedArea construct(AreaCreator<GroupedRegion> creator) {
        return new CraftGroupedArea(plugin, creator);
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftGroupedArea(plugin, world, name, tag);
    }
}
