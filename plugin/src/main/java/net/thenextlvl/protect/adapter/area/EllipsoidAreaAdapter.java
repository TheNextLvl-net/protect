package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftEllipsoidArea;

public class EllipsoidAreaAdapter extends RegionizedAreaAdapter<EllipsoidRegion, CraftEllipsoidArea> {
    public EllipsoidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "ellipsoid");
    }

    @Override
    protected CraftEllipsoidArea construct(CraftAreaCreator<EllipsoidRegion> creator) {
        return new CraftEllipsoidArea(creator);
    }
}
