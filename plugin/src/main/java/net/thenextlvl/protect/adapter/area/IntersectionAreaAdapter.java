package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.RegionIntersection;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftIntersectionArea;
import org.bukkit.World;

public class IntersectionAreaAdapter extends RegionizedAreaAdapter<RegionIntersection, CraftIntersectionArea> {
    public IntersectionAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "intersection");
    }

    @Override
    protected CraftIntersectionArea constructArea(String name, World world, RegionIntersection region, int priority) {
        return new CraftIntersectionArea(plugin.schematicFolder(), name, world, region, priority);
    }
}
