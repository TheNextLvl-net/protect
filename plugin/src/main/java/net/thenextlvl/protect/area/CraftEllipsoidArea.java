package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import core.nbt.tag.CompoundTag;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftEllipsoidArea extends CraftRegionizedArea<EllipsoidRegion> implements EllipsoidArea {
    public CraftEllipsoidArea(ProtectPlugin plugin, AreaCreator<EllipsoidRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftEllipsoidArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        super(plugin, world, name, tag);
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
