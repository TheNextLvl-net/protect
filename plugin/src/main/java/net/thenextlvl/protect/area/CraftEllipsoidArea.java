package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.World;

import java.io.File;

@TypesAreNotNullByDefault
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftEllipsoidArea extends CraftRegionizedArea<EllipsoidRegion> implements EllipsoidArea {
    public CraftEllipsoidArea(File schematicFolder, @NamePattern.Regionized String name, World world, EllipsoidRegion region, int priority) {
        super(schematicFolder, name, world, region, priority);
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
