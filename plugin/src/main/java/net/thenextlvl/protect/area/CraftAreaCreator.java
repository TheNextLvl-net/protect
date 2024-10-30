package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaCreateEvent;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Getter
@Setter
@NullMarked
@Accessors(fluent = true, chain = true)
public class CraftAreaCreator<T extends Region> implements AreaCreator<T> {
    private final ProtectPlugin plugin;

    private String name;
    private World world;
    private T region;

    private @Nullable String parent = null;
    private @Nullable UUID owner = null;
    private Map<Flag<?>, @Nullable Object> flags = new LinkedHashMap<>();
    private Set<UUID> members = new HashSet<>();
    private int priority = 0;

    public CraftAreaCreator(ProtectPlugin plugin, String name, World world, T region, @Nullable String parent,
                            @Nullable UUID owner, Map<Flag<?>, @Nullable Object> flags, Set<UUID> members, int priority) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.region = region;
        this.parent = parent;
        this.owner = owner;
        this.flags = flags;
        this.members = members;
        this.priority = priority;
    }

    public CraftAreaCreator(ProtectPlugin plugin, String name, World world, T region) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.region = region;
    }

    @Override
    public AreaCreator<T> copy() {
        return new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority);
    }

    @Override
    public AreaCreator<T> parent(@Nullable String parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public AreaCreator<T> parent(@Nullable RegionizedArea<?> area) {
        return parent(area == null ? null : area.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public RegionizedArea<T> create() throws UnsupportedRegionException, CircularInheritanceException {
        var area = plugin.areaService().getWrapper((Class<T>) region().getClass())
                .orElseThrow(() -> new UnsupportedRegionException(region().getClass()))
                .apply(this);
        plugin.areaProvider().persist(area);
        new AreaCreateEvent(area).callEvent();
        return area;
    }
}
