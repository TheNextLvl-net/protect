package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftCylinderArea;
import org.bukkit.World;

public class CylinderAreaAdapter extends RegionizedAreaAdapter<CylinderRegion, CraftCylinderArea> {
    public CylinderAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cylinder");
    }

    @Override
    protected CraftCylinderArea constructArea(String name, World world, CylinderRegion region, int priority) {
        return new CraftCylinderArea(plugin.schematicFolder(), name, world, region, priority);
    }

}
