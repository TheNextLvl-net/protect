package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftCylinderArea;

public class CylinderAreaAdapter extends RegionizedAreaAdapter<CylinderRegion, CraftCylinderArea> {
    public CylinderAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cylinder");
    }

    @Override
    protected CraftCylinderArea construct(CraftAreaCreator<CylinderRegion> creator) {
        return new CraftCylinderArea(creator);
    }
}
