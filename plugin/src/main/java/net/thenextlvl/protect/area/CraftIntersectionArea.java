package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.RegionIntersection;
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
public class CraftIntersectionArea extends CraftRegionizedArea<RegionIntersection> implements IntersectionArea {
    public CraftIntersectionArea(ProtectPlugin plugin,
                           @NamePattern.Regionized String name,
                           World world,
                           RegionIntersection region,
                           int priority,
                           @Nullable @NamePattern.Regionized String parent,
                           @Nullable UUID owner,
                           Set<UUID> members,
                           Map<Flag<?>, @Nullable Object> flags) {
        super(plugin, name, world, region, priority, parent, owner, members, flags);
    }

    public CraftIntersectionArea(ProtectPlugin plugin, AreaCreator<RegionIntersection> creator) {
        super(plugin, creator);
    }
}
