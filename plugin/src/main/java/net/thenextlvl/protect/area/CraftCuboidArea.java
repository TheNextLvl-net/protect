package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.World;

public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(ProtectPlugin plugin, @NamePattern String name, World world, CuboidRegion region, int priority) {
        super(plugin, name, world, region, priority);
    }
}
