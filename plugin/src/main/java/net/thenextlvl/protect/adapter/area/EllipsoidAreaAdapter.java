package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import core.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EllipsoidAreaAdapter extends RegionizedAreaAdapter<EllipsoidRegion, CraftEllipsoidArea> {
    public EllipsoidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "ellipsoid");
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftEllipsoidArea(plugin, world, name, tag);
    }
}
