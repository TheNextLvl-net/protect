package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftCylinderArea extends CraftRegionizedArea<CylinderRegion> implements CylinderArea {
    public CraftCylinderArea(@NotNull ProtectPlugin plugin, @NotNull AreaCreator<CylinderRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }

    @Override
    public @NotNull CylinderRegionSelector getRegionSelector() {
        return new CylinderRegionSelector(
                getRegion().getWorld(),
                getRegion().getCenter().toVector2().toBlockPoint(),
                getRegion().getRadius(),
                getRegion().getMinimumY(),
                getRegion().getMaximumY()
        );
    }
}
