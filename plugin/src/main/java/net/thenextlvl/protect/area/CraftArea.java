package net.thenextlvl.protect.area;

import lombok.*;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.flag.AreaFlagChangeEvent;
import net.thenextlvl.protect.area.event.flag.AreaFlagResetEvent;
import net.thenextlvl.protect.area.event.inheritance.AreaPriorityChangeEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class CraftArea implements Area {
    protected final ProtectPlugin plugin;

    @NamePattern
    private final @Getter String name;
    private final @Getter World world;

    private Map<Flag<?>, @Nullable Object> flags;
    private @Getter int priority;

    @Override
    public @NotNull LinkedHashSet<Area> getParents() {
        var parents = new LinkedHashSet<Area>();
        var parent = getParent().orElse(null);
        while (parent != null && parent != this && parents.add(parent))
            parent = parent.getParent().orElse(null);
        return parents;
    }

    @Override
    public @NotNull Map<Flag<?>, @Nullable Object> getFlags() {
        return Map.copyOf(flags);
    }

    public boolean setPriority(int priority) {
        if (this.priority == priority) return false;
        var event = new AreaPriorityChangeEvent(this, priority);
        if (!event.callEvent()) return false;
        this.priority = priority;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFlag(@NotNull Flag<T> flag) {
        var value = (T) getFlags().get(flag);
        if (value != null) return value;
        return getParent().map(area -> area.getFlag(flag))
                .orElseGet(flag::defaultValue);
    }

    @Override
    public <T> boolean setFlag(@NotNull Flag<T> flag, T state) {
        var event = new AreaFlagChangeEvent<>(this, flag, state);
        if (!event.callEvent()) return false;
        return !Objects.equals(flags.put(flag, event.getNewState()), event.getNewState());
    }

    @Override
    public <T> boolean removeFlag(@NotNull Flag<T> flag) {
        var event = new AreaFlagResetEvent<>(this, flag);
        return event.callEvent() && flags.remove(flag) != null;
    }

    @Override
    public <T> boolean hasFlag(@NotNull Flag<T> flag) {
        return flags.containsKey(flag);
    }

    @Override
    public @NotNull List<Entity> getHighestEntities() {
        return getEntities().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    public @NotNull List<Player> getHighestPlayers() {
        return getPlayers().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    public int compareTo(@NotNull Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }
}
