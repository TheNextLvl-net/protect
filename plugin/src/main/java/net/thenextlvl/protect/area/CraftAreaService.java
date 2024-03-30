package net.thenextlvl.protect.area;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaCreateEvent;
import net.thenextlvl.protect.event.AreaDeleteEvent;
import org.bukkit.World;

@RequiredArgsConstructor
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftAreaService implements AreaService {
    private final ProtectPlugin plugin;

    @Override
    public CuboidArea create(@NamePattern String name, World world, BlockVector3 pos1, BlockVector3 pos2) {
        return create(name, world, new CuboidRegion(pos1, pos2));
    }

    @Override
    public CuboidArea create(@NamePattern String name, World world, CuboidRegion region) {
        var area = new CraftCuboidArea(plugin, name, world, region, 0);
        plugin.loadAreas(area.getWorld()).getRoot().add(area);
        new AreaCreateEvent(area).callEvent();
        return area;
    }

    @Override
    public <T extends Region> boolean delete(RegionizedArea<T> area) {
        if (!new AreaDeleteEvent(area).callEvent()) return false;
        return plugin.loadAreas(area.getWorld()).getRoot().remove(area);
    }
}
