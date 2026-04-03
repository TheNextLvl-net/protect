package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CraftEllipsoidArea extends CraftRegionizedArea<EllipsoidRegion> implements EllipsoidArea {
    public CraftEllipsoidArea(final ProtectPlugin plugin, final AreaCreator<EllipsoidRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftEllipsoidArea(final ProtectPlugin plugin, final World world, final String name, final CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected EllipsoidRegion readRegion(final CompoundTag tag) {
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
