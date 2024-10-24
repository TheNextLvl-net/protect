package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import core.annotation.TypesAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@TypesAreNotNullByDefault
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftCylinderArea extends CraftRegionizedArea<CylinderRegion> implements CylinderArea {
    public CraftCylinderArea(ProtectPlugin plugin,
                           @NamePattern.Regionized String name,
                           World world,
                           CylinderRegion region,
                           int priority,
                           @Nullable @NamePattern.Regionized String parent,
                           @Nullable UUID owner,
                           Set<UUID> members,
                           Map<Flag<?>, @Nullable Object> flags) {
        super(plugin, name, world, region, priority, parent, owner, members, flags);
    }

    public CraftCylinderArea(ProtectPlugin plugin, AreaCreator<CylinderRegion> creator) {
        super(plugin, creator);
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
