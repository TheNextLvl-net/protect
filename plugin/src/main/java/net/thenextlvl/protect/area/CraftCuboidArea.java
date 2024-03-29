package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.protect.ProtectPlugin;

public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(ProtectPlugin plugin, @NamePattern String name, CuboidRegion region, int priority) {
        super(plugin, name, region, priority);
    }
}
