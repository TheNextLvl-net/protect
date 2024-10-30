package net.thenextlvl.protect.area;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftGroupedArea extends CraftRegionizedArea<GroupedRegion> implements GroupedArea {
    public CraftGroupedArea(ProtectPlugin plugin, AreaCreator<GroupedRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }
}
