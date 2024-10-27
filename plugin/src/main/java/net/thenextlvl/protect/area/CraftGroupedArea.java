package net.thenextlvl.protect.area;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CraftGroupedArea extends CraftRegionizedArea<@NotNull GroupedRegion> implements GroupedArea {
    public CraftGroupedArea(ProtectPlugin plugin,
                            @NamePattern.Regionized String name,
                            World world,
                            GroupedRegion region,
                            int priority,
                            @Nullable @NamePattern.Regionized String parent,
                            @Nullable UUID owner,
                            Set<UUID> members,
                            Map<Flag<?>, @Nullable Object> flags) throws CircularInheritanceException {
        super(plugin, name, world, region, priority, parent, owner, members, flags);
    }

    public CraftGroupedArea(ProtectPlugin plugin, AreaCreator<@NotNull GroupedRegion> creator) throws CircularInheritanceException {
        super(plugin, creator);
    }
}
