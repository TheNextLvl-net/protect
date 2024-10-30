package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(ProtectPlugin plugin, AreaCreator<CuboidRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }
}
