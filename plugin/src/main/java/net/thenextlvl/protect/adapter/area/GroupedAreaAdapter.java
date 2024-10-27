package net.thenextlvl.protect.adapter.area;

import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.region.GroupedRegion;

public class GroupedAreaAdapter extends RegionizedAreaAdapter<GroupedRegion, CraftGroupedArea> {
    public GroupedAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "grouped");
    }

    @Override
    protected CraftGroupedArea construct(CraftAreaCreator<GroupedRegion> creator) {
        return new CraftGroupedArea(creator.plugin(), creator);
    }
}
