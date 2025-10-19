package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.AreaCreateEvent;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.*;

@NullMarked
public final class CraftAreaCreator<T extends Region> implements AreaCreator<T> {
    private final ProtectPlugin plugin;

    private String name;
    private World world;
    private final T region;

    private @Nullable String parent = null;
    private @Nullable UUID owner = null;
    private Map<Flag<?>, @Nullable Object> flags = new HashMap<>();
    private Set<UUID> members = new HashSet<>();
    private int priority = 0;

    @SuppressWarnings("unchecked")
    public CraftAreaCreator(ProtectPlugin plugin, String name, World world, T region, @Nullable String parent,
                            @Nullable UUID owner, Map<Flag<?>, @Nullable Object> flags, Set<UUID> members, int priority) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.region = (T) region.clone();
        this.parent = parent;
        this.owner = owner;
        this.flags = new HashMap<>(flags);
        this.members = new HashSet<>(members);
        this.priority = priority;
    }

    @SuppressWarnings("unchecked")
    public CraftAreaCreator(ProtectPlugin plugin, String name, World world, T region) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.region = (T) region.clone();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public @Nullable UUID owner() {
        return owner;
    }

    @Override
    public AreaCreator<T> copy() {
        return new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority);
    }

    @Override
    public <V extends Region> AreaCreator<V> region(V region) {
        return new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority);
    }

    @Override
    public AreaCreator<T> world(World world) {
        this.world = world;
        return this;
    }

    @Override
    public Map<Flag<?>, @Nullable Object> flags() {
        return flags;
    }

    @Override
    public AreaCreator<T> flags(Map<Flag<?>, @Nullable Object> flags) {
        this.flags = new HashMap<>(flags);
        return this;
    }

    @Override
    public AreaCreator<T> members(Set<UUID> members) {
        this.members = new HashSet<>(members);
        return this;
    }

    @Override
    public AreaCreator<T> name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public AreaCreator<T> owner(@Nullable UUID owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public AreaCreator<T> parent(@Nullable String parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public AreaCreator<T> parent(@Nullable Area area) {
        return parent(area == null ? null : area.getName());
    }

    @Override
    public AreaCreator<T> priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RegionizedArea<T> create() throws UnsupportedRegionException, CircularInheritanceException, IOException {
        var area = plugin.areaService().getWrapper((Class<T>) region().getClass())
                .orElseThrow(() -> new UnsupportedRegionException(region().getClass()))
                .apply(this);
        plugin.areaProvider().persist(area);
        new AreaCreateEvent(area).callEvent();
        return area;
    }

    @Override
    public Set<UUID> members() {
        return members;
    }

    @Override
    public @Nullable String parent() {
        return parent;
    }

    @Override
    public T region() {
        return region;
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    public int priority() {
        return priority;
    }
}
