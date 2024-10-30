package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import core.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import net.thenextlvl.protect.area.CraftCuboidArea;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CuboidAreaAdapter extends RegionizedAreaAdapter<CuboidRegion, CraftCuboidArea> {
    public CuboidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cuboid");
    }

    @Override
    protected CraftCuboidArea construct(AreaCreator<CuboidRegion> creator) {
        return new CraftCuboidArea(plugin, creator);
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftCuboidArea(plugin, world, name, tag);
    }
}
