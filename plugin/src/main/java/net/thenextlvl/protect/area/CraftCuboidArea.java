package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.CuboidRegion;
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
public class CraftCuboidArea extends CraftRegionizedArea<CuboidRegion> implements CuboidArea {
    public CraftCuboidArea(ProtectPlugin plugin,
                           @NamePattern.Regionized String name,
                           World world,
                           CuboidRegion region,
                           int priority,
                           @Nullable @NamePattern.Regionized String parent,
                           @Nullable UUID owner,
                           Set<UUID> members,
                           Map<Flag<?>, @Nullable Object> flags) {
        super(plugin, name, world, region, priority, parent, owner, members, flags);
    }

    public CraftCuboidArea(CraftAreaCreator<CuboidRegion> creator) {
        super(creator);
    }
}
