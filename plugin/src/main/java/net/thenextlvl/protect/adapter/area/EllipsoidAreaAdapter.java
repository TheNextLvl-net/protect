package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import org.jetbrains.annotations.NotNull;

public class EllipsoidAreaAdapter extends RegionizedAreaAdapter<EllipsoidRegion, CraftEllipsoidArea> {
    public EllipsoidAreaAdapter(@NotNull ProtectPlugin plugin) {
        super(plugin, "ellipsoid");
    }

    @Override
    protected @NotNull CraftEllipsoidArea construct(@NotNull CraftAreaCreator<EllipsoidRegion> creator) {
        return new CraftEllipsoidArea(creator.plugin(), creator);
    }
}
