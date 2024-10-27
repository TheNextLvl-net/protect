package net.thenextlvl.protect.adapter.area;

import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.region.GroupedRegion;
import org.jetbrains.annotations.NotNull;

public class GroupedAreaAdapter extends RegionizedAreaAdapter<GroupedRegion, CraftGroupedArea> {
    public GroupedAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "grouped");
    }

    @Override
    protected CraftGroupedArea construct(@NotNull CraftAreaCreator<GroupedRegion> creator) {
        return new CraftGroupedArea(creator.plugin(), creator);
    }
}
