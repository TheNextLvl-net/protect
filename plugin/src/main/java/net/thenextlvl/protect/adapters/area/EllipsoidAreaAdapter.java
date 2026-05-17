package net.thenextlvl.protect.adapters.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EllipsoidAreaAdapter extends RegionizedAreaAdapter<EllipsoidRegion, CraftEllipsoidArea> {
    public EllipsoidAreaAdapter(final ProtectPlugin plugin) {
        super(plugin, "ellipsoid");
    }

    @Override
    public Area construct(final World world, final String name, final CompoundTag tag) {
        return new CraftEllipsoidArea(plugin, world, name, tag);
    }
}
