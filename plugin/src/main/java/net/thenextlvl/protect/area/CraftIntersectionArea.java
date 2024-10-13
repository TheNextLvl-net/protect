package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.RegionIntersection;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.World;

import java.io.File;

@TypesAreNotNullByDefault
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftIntersectionArea extends CraftRegionizedArea<RegionIntersection> implements IntersectionArea {
    public CraftIntersectionArea(File schematicFolder, @NamePattern.Regionized String name, World world, RegionIntersection region, int priority) {
        super(schematicFolder, name, world, region, priority);
    }
}
