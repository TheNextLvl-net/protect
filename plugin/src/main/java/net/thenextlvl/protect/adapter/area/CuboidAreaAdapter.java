package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftCuboidArea;
import org.bukkit.World;

public class CuboidAreaAdapter extends RegionizedAreaAdapter<CuboidRegion, CraftCuboidArea> {
    public CuboidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cuboid");
    }

    @Override
    protected CraftCuboidArea constructArea(String name, World world, CuboidRegion region, int priority) {
        return new CraftCuboidArea(plugin.schematicFolder(), name, world, region, priority);
    }
}
