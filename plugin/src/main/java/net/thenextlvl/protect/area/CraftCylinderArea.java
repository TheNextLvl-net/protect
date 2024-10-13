package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.World;

import java.io.File;

@TypesAreNotNullByDefault
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftCylinderArea extends CraftRegionizedArea<CylinderRegion> implements CylinderArea {
    public CraftCylinderArea(File schematicFolder, @NamePattern.Regionized String name, World world, CylinderRegion region, int priority) {
        super(schematicFolder, name, world, region, priority);
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
