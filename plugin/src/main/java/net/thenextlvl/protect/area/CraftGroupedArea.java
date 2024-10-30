package net.thenextlvl.protect.area;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftGroupedArea extends CraftRegionizedArea<@NotNull GroupedRegion> implements GroupedArea {
    public CraftGroupedArea(@NotNull ProtectPlugin plugin, @NotNull AreaCreator<GroupedRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }
}
