package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import org.bukkit.World;

public class EllipsoidAreaAdapter extends RegionizedAreaAdapter<EllipsoidRegion, CraftEllipsoidArea> {
    public EllipsoidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "ellipsoid");
    }

    @Override
    protected CraftEllipsoidArea constructArea(String name, World world, EllipsoidRegion region, int priority) {
        return new CraftEllipsoidArea(plugin.schematicFolder(), name, world, region, priority);
    }
}
