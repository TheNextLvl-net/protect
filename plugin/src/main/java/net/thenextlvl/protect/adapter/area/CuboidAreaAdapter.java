package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftCuboidArea;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CuboidAreaAdapter extends RegionizedAreaAdapter<CuboidRegion, CraftCuboidArea> {
    public CuboidAreaAdapter(ProtectPlugin plugin) {
        super(plugin, "cuboid");
    }

    @Override
    protected CraftCuboidArea construct(CraftAreaCreator<CuboidRegion> creator) {
        return new CraftCuboidArea(creator.plugin(), creator);
    }
}
