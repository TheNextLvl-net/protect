package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.World;

import java.io.File;

@TypesAreNotNullByDefault
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(File schematicFolder, @NamePattern.Regionized String name, World world, CuboidRegion region, int priority) {
        super(schematicFolder, name, world, region, priority);
    }
}
