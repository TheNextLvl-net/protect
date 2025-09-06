package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftCylinderArea extends CraftRegionizedArea<CylinderRegion> implements CylinderArea {
    public CraftCylinderArea(ProtectPlugin plugin, AreaCreator<CylinderRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    public CraftCylinderArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        super(plugin, world, name, tag);
    }

    @Override
    protected CylinderRegion readRegion(CompoundTag tag) {
        return plugin.nbt.deserialize(tag.getAsCompound("region"), CylinderRegion.class);
    }

    @Override
    public CylinderRegionSelector getRegionSelector() {
        return new CylinderRegionSelector(
                getRegion().getWorld(),
                getRegion().getCenter().toVector2().toBlockPoint(),
                getRegion().getRadius(),
                getRegion().getMinimumY(),
                getRegion().getMaximumY()
        );
    }
}
