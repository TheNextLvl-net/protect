package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaCreateEvent;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class CraftAreaCreator<T extends Region> implements AreaCreator<T> {
    private final @NotNull ProtectPlugin plugin;

    private @NotNull String name;
    private @NotNull World world;
    private @NotNull T region;

    private @Nullable String parent = null;
    private @Nullable UUID owner = null;
    private @NotNull Map<@NotNull Flag<?>, @Nullable Object> flags = new LinkedHashMap<>();
    private @NotNull Set<UUID> members = new HashSet<>();
    private int priority = 0;

    @Override
    public @NotNull AreaCreator<T> copy() {
        return new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority);
    }

    @Override
    public @NotNull AreaCreator<T> parent(@Nullable String parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public @NotNull AreaCreator<T> parent(@Nullable RegionizedArea<?> area) {
        return parent(area == null ? null : area.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull RegionizedArea<@NotNull T> create() throws UnsupportedRegionException, CircularInheritanceException {
        var area = plugin.areaService().getWrapper((Class<T>) region().getClass())
                .orElseThrow(() -> new UnsupportedRegionException(region().getClass()))
                .apply(this);
        plugin.areaProvider().persist(area);
        new AreaCreateEvent(area).callEvent();
        return area;
    }
}
