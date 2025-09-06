package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftEllipsoidArea extends CraftRegionizedArea<EllipsoidRegion> implements EllipsoidArea {
    public CraftEllipsoidArea(ProtectPlugin plugin, AreaCreator<EllipsoidRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftEllipsoidArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected EllipsoidRegion readRegion(CompoundTag tag) {
        return plugin.nbt.deserialize(tag.getAsCompound("region"), EllipsoidRegion.class);
    }

    @Override
    public EllipsoidRegionSelector getRegionSelector() {
        return new EllipsoidRegionSelector(
                getRegion().getWorld(),
                getRegion().getCenter().toBlockPoint(),
                getRegion().getRadius()
        );
    }
}
