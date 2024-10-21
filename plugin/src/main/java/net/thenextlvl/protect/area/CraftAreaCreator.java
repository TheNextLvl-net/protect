package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.*;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
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
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
@Accessors(fluent = true, chain = true)
public class CraftAreaCreator<T extends Region> implements AreaCreator<T> {
    private final ProtectPlugin plugin;

    @NamePattern.Regionized
    private @NotNull String name;
    private @NotNull World world;
    private @NotNull T region;

    @NamePattern
    private @Nullable String parent = null;
    private @Nullable UUID owner = null;
    private Map<Flag<?>, @Nullable Object> flags = new LinkedHashMap<>();
    private Set<UUID> members = new HashSet<>();
    private int priority = 0;

    @Override
    public AreaCreator<T> copy() {
        return new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority);
    }

    @Override
    public @NamePattern.Regionized String name() {
        return name;
    }

    @Override
    public @NamePattern @Nullable String parent() {
        return parent;
    }

    @Override
    public AreaCreator<T> parent(@NamePattern @Nullable String parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public AreaCreator<T> parent(@Nullable RegionizedArea<?> area) {
        return parent(area == null ? null : area.getName());
    }

    private static final Set<Wrapper<?>> wrappers = new HashSet<>();

    private record Wrapper<T extends Region>(
            Class<T> type,
            Function<CraftAreaCreator<T>, CraftRegionizedArea<T>> creator
    ) {
    }

    private static <T extends Region> void registerWrapper(Wrapper<T> wrapper) {
        wrappers.add(wrapper);
    }

    static {
        registerWrapper(new Wrapper<>(CuboidRegion.class, CraftCuboidArea::new));
        registerWrapper(new Wrapper<>(CylinderRegion.class, CraftCylinderArea::new));
        registerWrapper(new Wrapper<>(EllipsoidRegion.class, CraftEllipsoidArea::new));
        registerWrapper(new Wrapper<>(RegionIntersection.class, CraftIntersectionArea::new));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Region> Optional<Wrapper<T>> getWrapper(Class<T> type) {
        return wrappers.stream()
                .filter(wrapper -> wrapper.type().isAssignableFrom(type))
                .map(wrapper -> (Wrapper<T>) wrapper)
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CraftRegionizedArea<T> create() throws UnsupportedRegionException, CircularInheritanceException {
        var area = getWrapper((Class<T>) region().getClass()).orElseThrow(
                () -> new UnsupportedRegionException(region().getClass())
        ).creator().apply(this);
        plugin.areaProvider().persist(area);
        new AreaCreateEvent(area).callEvent();
        return area;
    }
}
