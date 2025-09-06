package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftCylinderArea;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CylinderAreaAdapter extends RegionizedAreaAdapter<CylinderRegion, CraftCylinderArea> {
    public CylinderAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cylinder");
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftCylinderArea(plugin, world, name, tag);
    }
}
