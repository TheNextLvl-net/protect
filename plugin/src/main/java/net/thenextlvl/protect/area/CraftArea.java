package net.thenextlvl.protect.area;

import lombok.*;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaFlagChangeEvent;
import net.thenextlvl.protect.event.AreaFlagUnsetEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class CraftArea implements Area {
    private final @Getter(AccessLevel.NONE) ProtectPlugin plugin = JavaPlugin.getPlugin(ProtectPlugin.class);
    private Map<Flag<?>, @Nullable Object> flags = new LinkedHashMap<>();
    private final String name;
    private final World world;
    private int priority;

    protected CraftArea(String name, World world, int priority) {
        this.name = name;
        this.world = world;
        this.priority = priority;
    }

    public boolean setPriority(int priority) {
        if (this.priority == priority) return false;
        var event = new AreaPriorityChangeEvent(this, priority);
        if (!event.callEvent()) return false;
        this.priority = priority;
        return true;
    }

    @Override
    public <T> void setFlag(@NotNull Flag<T> flag, T state) {
        var event = new AreaFlagChangeEvent<>(this, flag, state);
        if (event.callEvent()) Area.super.setFlag(flag, event.getNewState());
    }

    @Override
    public <T> boolean removeFlag(@NotNull Flag<T> flag) {
        var event = new AreaFlagUnsetEvent<>(this, flag);
        return event.callEvent() && Area.super.removeFlag(flag);
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
