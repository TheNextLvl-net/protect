package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import core.annotation.TypesAreNotNullByDefault;
import org.bukkit.World;

import java.io.File;

@TypesAreNotNullByDefault
public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(File schematicFolder, @NamePattern String name, World world, CuboidRegion region, int priority) {
        super(schematicFolder, name, world, region, priority);
    }
}
