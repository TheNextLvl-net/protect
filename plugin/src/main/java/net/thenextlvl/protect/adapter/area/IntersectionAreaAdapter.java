package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.RegionIntersection;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftIntersectionArea;

public class IntersectionAreaAdapter extends RegionizedAreaAdapter<RegionIntersection, CraftIntersectionArea> {
    public IntersectionAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "intersection");
    }

    @Override
    protected CraftIntersectionArea construct(CraftAreaCreator<RegionIntersection> creator) {
        return new CraftIntersectionArea(creator.plugin(), creator);
    }
}
