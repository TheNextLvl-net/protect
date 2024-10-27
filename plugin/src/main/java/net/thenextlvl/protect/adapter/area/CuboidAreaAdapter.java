package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftCuboidArea;
import org.jetbrains.annotations.NotNull;

public class CuboidAreaAdapter extends RegionizedAreaAdapter<CuboidRegion, CraftCuboidArea> {
    public CuboidAreaAdapter(@NotNull ProtectPlugin plugin) {
        super(plugin, "cuboid");
    }

    @Override
    protected @NotNull CraftCuboidArea construct(@NotNull CraftAreaCreator<CuboidRegion> creator) {
        return new CraftCuboidArea(creator.plugin(), creator);
    }
}
